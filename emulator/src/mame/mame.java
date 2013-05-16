/*
This file is part of Arcadeflex.

Arcadeflex is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Arcadeflex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Arcadeflex.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * ported to v0.28
 * ported to v0.27
 *
 *
 *
 */
package mame;


import arcadeflex.UrlDownloadProgress;
import arcadeflex.settings;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static mame.mameH.*;
import static mame.driver.*;
import static mame.usrintrfH.*;
import static mame.usrintrf.*;
import static mame.osdependH.*;
import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.common.*;
import static arcadeflex.osdepend.*;
import static mame.cpuintrf.*;
import static mame.commonH.*;
import static mame.inptport.*;


/**
 *
 * @author shadow
 */
public class mame {

    public static String mameversion = "0.28";
    static RunningMachine machine = new RunningMachine();
    public static RunningMachine Machine = machine;
    static GameDriver gamedrv;
    private static MachineDriver drv;
    public static int hiscoreloaded;
    public static String hiscorename;


    public static int frameskip;
    public static int throttle = 1;/* toggled by F10 */

    static int VolumePTR = 0;
    static int CurrentVolume = 100;
    static final int MAX_COLOR_TUPLE = 16;/* no more than 4 bits per pixel, for now */

    static final int MAX_COLOR_CODES = 256;/* no more than 256 color codes, for now */

    public static char[] RAM;
    public static char[] ROM;

    public static char[] remappedtable = new char[MAX_COLOR_TUPLE*MAX_COLOR_CODES];
    static final String DEFAULT_NAME = "pacman";
    public static FILE errorlog;
    public static boolean onlinerom = false;
    public static UrlDownloadProgress dlprogress;

    public static int main(int argc, String[] argv) {
        dlprogress = new UrlDownloadProgress();
        dlprogress.setVersion("arcadeflex version: "+settings.version);
        int i, j, list = 0, help=0, log, success;
        String gamename = null;
        for (i = 1; i < argc; i++) {
            if (argv[i].charAt(0) != '/') {
                argv[i].replaceFirst("/", "-");  /* covert '/' in '-' */
            }
        }
        help = BOOL(argc==1);
        for (i = 1;i < argc;i++)/* help me, please! */
        {
          if ((stricmp(argv[i], "-?") == 0) || (stricmp(argv[i], "-h") == 0) || (stricmp(argv[i], "-help") == 0)) {
            help = 1;
        }
        }
        if (help != 0)
        {
           printf(settings.version +" (based on mame v%s)\n", new Object[] { mameversion });
           showdisclaimer();
           printf("Usage:  java -jar arcadeflex.jar gamename [options]\n", new Object[0]);
           return 0;
        }

        for (i = 1; i < argc; i++) {  /* Front end utilities ;) */
            if (stricmp(argv[i], "-list") == 0) {
                list = 1;
            }
            if (stricmp(argv[i], "-listfull") == 0) {
                list = 2;
            }
            if (stricmp(argv[i], "-listroms") == 0) {
                list = 3;
            }
            if (stricmp(argv[i], "-listsamples") == 0) {
                list = 4;
            }
        }

        switch (list) {
            case 1:
                printf("\nArcadeFlex currently supports the following games:\n\n", new Object[0]);
                i = 0;
                while (drivers[i] != null) {
                    printf("%10s", new Object[]{drivers[i].name});
                    i++;
                    if (i % 7 != 0) {
                        continue;
                    }
                   printf("\n", new Object[0]);
                }
                if (i % 7 != 0) {
                    printf("\n", new Object[0]);
                }
                printf("\nTotal games supported: %4d\n", new Object[]{Integer.valueOf(i)});
                return 0;
            case 2:
                printf("NAMES:    DESCRIPTIONS:\n", new Object[0]);
                i = 0;
                while (drivers[i] != null) {
                   printf("%-10s\"%s\"\n", new Object[]{drivers[i].name, drivers[i].description});
                    i++;
                }
                return 0;
            case 3:/* game roms list */
            case 4:/* game samples list */
                gamename = (argc > 1) && (argv[1].charAt(0) != '-') ? argv[1] : DEFAULT_NAME;
                j = 0;
                while ((drivers[j] != null) && (stricmp(gamename, drivers[j].name) != 0)) {
                    j++;
                }
                if (drivers[j] == null) {
                   printf("game \"%s\" not supported\n", new Object[]{gamename});
                   dlprogress.setRomName("error in game name or game not supported");
                    return 1;
                }
                gamedrv = drivers[j];
                if (list == 3) {
                    printromlist(gamedrv.rom, gamename);
                } else if ((gamedrv.samplenames != null) && (gamedrv.samplenames[0] != null)) {
                    i = 0;
                    while (gamedrv.samplenames[i] != null) {
                       printf("%s\n", new Object[]{gamedrv.samplenames[i]});
                        i++;
                    }
                }
                return 0;
        }

        success = 1;

        log = 0;
        for (j = 1; j < argc; j++) {
            if (stricmp(argv[j], "-log") == 0) {
                log = 1;
            }
        }

        if (log != 0) {
            errorlog = fopen("error.log", "wa");
        }
        //nickblame: add online support----
        for (j = 1; j < argc; j++) {
            if (stricmp(argv[j], "-onlinerom") == 0) {
                onlinerom = true;
                dlprogress.setVisible(true);
            }
        }//--------------------------------

        if (init_machine((argc > 1) && (argv[1].charAt(0) != '-') ? argv[1] : DEFAULT_NAME, argc, argv) == 0) {
            if (osd_init(argc, argv) == 0) {
                if (run_machine((argc > 1) && (argv[1].charAt(0) != '-') ? argv[1] : DEFAULT_NAME) == 0) {
                    success = 0;
                } else {
                    printf("Unable to start emulation\n", new Object[0]);
                }

                osd_exit();
            } else {
                printf("Unable to initialize system\n", new Object[0]);
            }
            shutdown_machine();
        } else {
            printf("Unable to initialize machine emulation\n", new Object[0]);
        }
        if (errorlog != null) {
            fclose(errorlog);
        }
        
        return success;
    }
    /***************************************************************************

      Initialize the emulated machine (load the roms, initialize the various
      subsystems...). Returns 0 if successful.

    ***************************************************************************/
        public static int init_machine(String gamename, int argc, String[] argv)
        {
            int i;
            int nocheat;
            dlprogress.setRomName("loading game: "+gamename);
            frameskip = 0;
            for (i = 1; i < argc; i++) {
                if (stricmp(argv[i], "-frameskip") == 0) {
                    i++;
                    if (i < argc) {
                        frameskip = atoi(argv[i]);
                        if (frameskip < 0) {
                            frameskip = 0;
                        }
                        if (frameskip > 3) {
                            frameskip = 3;
                        }
                    }
                }
            }
            nocheat = 1;
            for (i = 1;i < argc;i++)
            {
                    if (stricmp(argv[i],"-cheat") == 0)
                            nocheat = 0;
            }

            i = 0;
            while (drivers[i] != null && stricmp(gamename, drivers[i].name) != 0) {
                i++;
            }

            if (drivers[i] == null) {
                printf("game \"%s\" not supported\n", gamename);
                return 1;
            }

            Machine.gamedrv = gamedrv = drivers[i];
            Machine.drv = drv = gamedrv.drv;
            
            Machine.orientation = gamedrv.orientation;
            for (i = 1;i < argc;i++)
            {
                    if (stricmp(argv[i],"-ror") == 0)
                    {
                            if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
                                    Machine.orientation ^= ORIENTATION_ROTATE_180;

                            Machine.orientation ^= ORIENTATION_ROTATE_90;
                    }
            }
            for (i = 1;i < argc;i++)
            {
                    if (stricmp(argv[i],"-rol") == 0)
                    {
                            if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
                                    Machine.orientation ^= ORIENTATION_ROTATE_180;

                            Machine.orientation ^= ORIENTATION_ROTATE_270;
                    }
            }
            for (i = 1;i < argc;i++)
            {
                    if (stricmp(argv[i],"-flipx") == 0)
                            Machine.orientation ^= ORIENTATION_FLIP_X;
            }
            for (i = 1;i < argc;i++)
            {
                    if (stricmp(argv[i],"-flipy") == 0)
                            Machine.orientation ^= ORIENTATION_FLIP_Y;
            }
            if (gamedrv.new_input_ports!=null)
            {
                int total;
		NewInputPort from;
                int ptr=0;
		
                NewInputPort[] gamedrv_clone=null;
                
		total = 0;
		do
		{       from = gamedrv.new_input_ports[ptr];
			total++;
                        ptr++;
		} while (from.type != IPT_END);
                ///java needed code. Create a clone of gamedrv.new_input_ports for not destroying it while copy to Machine.input_ports
                gamedrv_clone=new NewInputPort[total];
                for(int k=0; k<total; k++)
                {
                    try {
                        gamedrv_clone[k]=(NewInputPort)gamedrv.new_input_ports[k].deepClone();
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(mame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //end of java code
                if((Machine.input_ports = new NewInputPort[total])==null)
                    return 1;
                from=null;
                int ptr2=0;		
         	do
		{       
                        from = gamedrv_clone[ptr2];
                        Machine.input_ports[ptr2]=from;
                        if(nocheat!=0 && (Machine.input_ports[ptr2].type & IPF_CHEAT)!=0)
                        {
                            Machine.input_ports[ptr2].type=IPT_UNUSED;/* disable cheats */
                        }
			ptr2++;
		} while (from.type != IPT_END);  
                
            }
            if (readroms(gamedrv.rom, "roms/" + gamename) != 0) {  /*shadow note : added extra path for roms*/
                machine.input_ports=null;
                return 1;
            }

            RAM = Machine.memory_region[drv.cpu[0].memory_region];
            ROM = RAM;

            /* decrypt the ROMs if necessary */
            if (gamedrv.rom_decode != null) {
                gamedrv.rom_decode.handler();
            }
            if (gamedrv.opcode_decode != null) {
                int j;
                /* find the first available memory region pointer */
                j = 0;
                while (Machine.memory_region[j] != null) {
                    j++;
                }

                if ((ROM = new char[0x10000]) == null) {
                   Machine.input_ports=null;
		    /* TODO: should also free the allocated memory regions */
                    return 1;
                }
                Machine.memory_region[j] = ROM;

                gamedrv.opcode_decode.handler();
            }

            /* read audio samples if available */
            Machine.samples = null; //readsamples(gamedrv.samplenames,gamename);  //TODOO!!!!!!

            /* first of all initialize the memory handlers, which could be used by the */
            /* other initialization routines */
            cpu_init();

            if (drv.vh_init != null && drv.vh_init.handler(gamename) != 0) {
                /* TODO: should also free the resources allocated before */
                return 1;
            }

            if (drv.sh_init != null && drv.sh_init.handler(gamename) != 0) {
                  /* TODO: should also free the resources allocated before */
                return 1;
            }
            return 0;
    }
    static void shutdown_machine()
    {
        int i;


        /* free audio samples */
        //freesamples(Machine.samples);//TODO samples
        Machine.samples=null;

        /* free the memory allocated for ROM and RAM */
        for (i = 0; i < MAX_MEMORY_REGIONS; i++) {
            Machine.memory_region[i] = null;
        }
        /* free the memory allocated for input ports definition */
	Machine.input_ports = null;
    }
    static void vh_close()
    {
        int i;

        for (i = 0; i < MAX_GFX_ELEMENTS; i++) {
            freegfx(Machine.gfx[i]);
            Machine.gfx[i] = null;
        }
        freegfx(Machine.uifont);
        Machine.uifont = null;
        osd_close_display();
    }

    static int vh_open() {
        int i;
        char[] palette;
        char[] colortable;
        char convpalette[] = new char[3 * MAX_PENS];
        char convtable[] = new char[MAX_COLOR_TUPLE * MAX_COLOR_CODES];

        for (i = 0; i < MAX_GFX_ELEMENTS; i++) {
            Machine.gfx[i] = null;
        }

        Machine.uifont = null;
        
        /* convert the gfx ROMs into character sets. This is done BEFORE calling the driver's */
	/* convert_color_prom() routine because it might need to check the Machine->gfx[] data */
        if (drv.gfxdecodeinfo != null) {
            for (i = 0; i < MAX_GFX_ELEMENTS && drv.gfxdecodeinfo[i].memory_region != -1; i++) {
                if ((Machine.gfx[i] = decodegfx(new CharPtr(Machine.memory_region[drv.gfxdecodeinfo[i].memory_region], drv.gfxdecodeinfo[i].start), drv.gfxdecodeinfo[i].gfxlayout)) == null) {
                    vh_close();
                    return 1;
                }
                Machine.gfx[i].colortable = new CharPtr(remappedtable, drv.gfxdecodeinfo[i].color_codes_start);
                Machine.gfx[i].total_colors = drv.gfxdecodeinfo[i].total_color_codes;
            }
        }
        /* convert the palette */
        if (drv.vh_convert_color_prom != null) {
            drv.vh_convert_color_prom.handler(convpalette, convtable, gamedrv.color_prom);
            palette = convpalette;
            colortable = convtable;
        } else {
            palette = gamedrv.palette;
            colortable = gamedrv.colortable;
        }
        
        /* create the display bitmap, and allocate the palette */  
        if ((Machine.scrbitmap = osd_create_display(drv.screen_width, drv.screen_height,drv.video_attributes)) == null) {
            return 1;
        }

        
        for (i = 0; i < drv.total_colors; i++) {
            Machine.pens[i] = (char) osd_obtain_pen(palette[3 * i], palette[3 * i + 1], palette[3 * i + 2]);
        }

        for (i = 0; i < drv.color_table_len; i++) {
            remappedtable[i] = Machine.pens[colortable[i]];
        }


        

        /* build our private user interface font */
        if ((Machine.uifont = builduifont()) == null) {
            vh_close();
            return 1;
        }


        /* free the graphics ROMs, they are no longer needed */
        Machine.memory_region[1] = null;

        return 0;
    }
    /***************************************************************************

      This function takes care of refreshing the screen, processing user input,
      and throttling the emulation speed to obtain the required frames per second.

    ***************************************************************************/
     static int framecount = 0;
     static int showfpstemp = 0;
     static int showvoltemp = 0;
     static int showfps;
     static int f11pressed;
     static int f10pressed;
     static final int MEMORY = 10;
     static long[] prev = new long[10];
     static int clock_counter;
     static int speed;
     static int updatescreen()
     {
        /* read hi scores from disk */
        if ((hiscoreloaded == 0) && (gamedrv.hiscore_load != null)) {
            hiscoreloaded = gamedrv.hiscore_load.handler();
        }

        /* if the user pressed ESC, stop the emulation */
        if (osd_key_pressed(OSD_KEY_ESC)) {
            return 1;
        }

        /* if the user pressed F3, reset the emulation */
        if (osd_key_pressed(OSD_KEY_F3)) {
            machine_reset();
        }
        if (osd_key_pressed(OSD_KEY_F9)) {
            if (++VolumePTR > 4) {
                VolumePTR = 0;
            }
            CurrentVolume = (4 - VolumePTR) * 25;
            osd_set_mastervolume(CurrentVolume);
            while (osd_key_pressed(OSD_KEY_F9)) {
                if (drv.sh_update != null) {
                    drv.sh_update.handler();
                    osd_update_audio();
                }
                osd_refresh();
            }
            showvoltemp = 50;
        }

        if (osd_key_pressed(OSD_KEY_F8)) {                                          /* change frameskip */
            if (frameskip < 3) {
                frameskip++;
            } else {
                frameskip = 0;
            }
            while (osd_key_pressed(OSD_KEY_F8));
            showfpstemp = 50;
        }

        if (osd_key_pressed(OSD_KEY_MINUS_PAD)) /* decrease volume */ {
            if (CurrentVolume > 0) {
                CurrentVolume--;
            }
            osd_set_mastervolume(CurrentVolume);
            showvoltemp = 50;
        }

        if (osd_key_pressed(OSD_KEY_PLUS_PAD)) /* increase volume */ {
            if (CurrentVolume < 100) {
                CurrentVolume++;
            }
            osd_set_mastervolume(CurrentVolume);
            showvoltemp = 50;
        }

        if (osd_key_pressed(OSD_KEY_P)) /* pause the game */ {
            DisplayText[] dt =DisplayText.create(2);
            
            dt[0].text = "PAUSED";
            dt[0].color = DT_COLOR_RED;
            dt[0].x = (Machine.scrbitmap.width - Machine.uifont.width * strlen(dt[0].text)) / 2;
            dt[0].y = (Machine.scrbitmap.height - Machine.uifont.height) / 2;
            dt[1].text = null;
            displaytext(dt, 0);

            osd_set_mastervolume(0);

            while (osd_key_pressed(OSD_KEY_P)) {
                osd_update_audio();	/* give time to the sound hardware to apply the volume change */
            }
            osd_refresh();
           while (!osd_key_pressed(OSD_KEY_P)  && !osd_key_pressed(OSD_KEY_ESC))
	   {
			if (osd_key_pressed(OSD_KEY_TAB)) setup_menu();	/* call the configuration menu */

			osd_clearbitmap(Machine.scrbitmap);
                        drv.vh_update.handler(Machine.scrbitmap);/* redraw screen */
			
			if (uclock() % UCLOCKS_PER_SEC < UCLOCKS_PER_SEC/2)
				displaytext(dt,0);	/* make PAUSED blink */
			osd_update_display();
	   }

	   while (osd_key_pressed(OSD_KEY_ESC));	/* wait for jey release */
	   while (osd_key_pressed(OSD_KEY_P));	/* ditto */

			osd_set_mastervolume(CurrentVolume);
			osd_clearbitmap(Machine.scrbitmap);
        }

        /* if the user pressed TAB, go to the setup menu */
        if (osd_key_pressed(OSD_KEY_TAB)) {
            osd_set_mastervolume(0);

            while (osd_key_pressed(OSD_KEY_TAB)) {
                osd_update_audio();	/* give time to the sound hardware to apply the volume change */
                osd_refresh();
            }
            if (setup_menu() != 0) {
                return 1;
            }

            osd_set_mastervolume(CurrentVolume);
        }

        /* if the user pressed F4, show the character set */
        if (osd_key_pressed(OSD_KEY_F4)) {
            osd_set_mastervolume(0);

            while (osd_key_pressed(OSD_KEY_F4)) {
                osd_update_audio();	/* give time to the sound hardware to apply the volume change */
                osd_refresh();
            }
            if (showcharset() != 0) {
                return 1;
            }

            osd_set_mastervolume(CurrentVolume);
        }


        if (++framecount > frameskip) {
            framecount = 0;

            if (osd_key_pressed(OSD_KEY_F11)) {
                if (f11pressed == 0) {
                    showfps ^= 1;
                    if (showfps == 0) {
                        osd_clearbitmap(Machine.scrbitmap);
                    }
                }
                f11pressed = 1;
            } else {
                f11pressed = 0;
            }

            if (showfpstemp != 0) {
                showfpstemp--;
                if ((showfps == 0) && (showfpstemp == 0)) {
                    osd_clearbitmap(Machine.scrbitmap);
                }
            }

            if (showvoltemp != 0) {
                showvoltemp--;
                if (showvoltemp == 0) {
                    osd_clearbitmap(Machine.scrbitmap);
                }
            }

            if (osd_key_pressed(OSD_KEY_F10)) {
                if (f10pressed == 0) {
                    throttle ^= 1;
                }
                f10pressed = 1;
            } else {
                f10pressed = 0;
            }


            drv.vh_update.handler(Machine.scrbitmap);	/* update screen */

            if ((showfps != 0) || (showfpstemp != 0)) {
                	int trueorientation;
			int fps,i,l;
			char buf[]=new char[30];
                        


			/* hack: force the display into standard orientation to avoid */
			/* rotating the text */
			trueorientation = Machine.orientation;
			Machine.orientation = ORIENTATION_DEFAULT;

			fps = (Machine.drv.frames_per_second * speed + 50) / 100;
                        sprintf(buf, " %d%% (%-3dfps)", new Object[] { speed,fps });
			l = strlen(buf);
			for (i = 0;i < l;i++)
				drawgfx(Machine.scrbitmap,Machine.uifont,buf[i],DT_COLOR_WHITE,0,0,Machine.scrbitmap.width-(l-i)*Machine.uifont.width,0,null,TRANSPARENCY_NONE,0);
              
			Machine.orientation = trueorientation;
                        
            }

            if (showvoltemp != 0) {                     /* volume-meter */
                int i, x;
                char[] volstr = new char[25];

                x = (drv.screen_width - 24 * Machine.uifont.width) / 2;
                strcpy(volstr, "                      ");
                for (int k = 0; k < CurrentVolume / 5; k++) {
                    volstr[(k + 1)] = '\025';
                }

               drawgfx(Machine.scrbitmap, Machine.uifont, 16, 2, 0, 0, x, drv.screen_height / 2, null, 0, 0);
                drawgfx(Machine.scrbitmap, Machine.uifont, 17, 2, 0, 0, x + 23 * Machine.uifont.width, drv.screen_height / 2, null, 0, 0);
                for (i = 0; i < 22; i++) {
                    drawgfx(Machine.scrbitmap, Machine.uifont, volstr[i], 0, 0, 0, x + (i + 1) * Machine.uifont.width, drv.screen_height / 2, null, 0, 0);
                }
            }
  ///////////////osd_poll_joystick();//TODO ??
            long curr;
            /* now wait until it's time to trigger the interrupt */
            do {

                curr = uclock();
            } while ((throttle != 0) && (curr - prev[clock_counter] < (frameskip + 1) * 1000000000 / drv.frames_per_second));
            //while (throttle != 0 && video_sync == 0 && (curr - prev[i]) < (frameskip+1) * UCLOCKS_PER_SEC/drv.frames_per_second);

            osd_update_display();

            clock_counter = (clock_counter + 1) % MEMORY;
		if ((curr - prev[clock_counter])!=0)
		{
			long divdr = drv.frames_per_second * (curr - prev[clock_counter]) / (100L * MEMORY);


			speed = (int)((UCLOCKS_PER_SEC * (frameskip+1) + divdr/2L) / divdr);
		}

		prev[clock_counter] = curr;
        }

        /* update audio. Do it after the speed throttling to be in better sync. */
        if (drv.sh_update != null) {
            drv.sh_update.handler();
            osd_update_audio();
        }


        return 0;
    }
     /***************************************************************************

      Run the emulation. Start the various subsystems and the CPU emulation.
      Returns non zero in case of error.

     ***************************************************************************/
      public static int run_machine(String gamename) 
      {
        int res = 1;
        

        if (vh_open() == 0) {
            if ((drv.vh_start == null) || (drv.vh_start.handler() == 0)) /* start the video hardware */ {
                if ((drv.sh_start == null) || (drv.sh_start.handler() == 0)) /* start the audio hardware */ {

                    
                    int i;                  
                    DisplayText[] dt = DisplayText.create(2);


                    dt[0].text = "PLEASE DO NOT DISTRIBUTE THE SOURCE CODE AND/OR THE EXECUTABLE "
                            + "APPLICATION WITH ANY ROM IMAGES.\n"
                            + "DOING AS SUCH WILL HARM ANY FURTHER DEVELOPMENT OF MAME AND COULD "
                            + "RESULT IN LEGAL ACTION BEING TAKEN BY THE LAWFUL COPYRIGHT HOLDERS "
                            + "OF ANY ROM IMAGES.\n\n"
                            + "IF YOU DO NOT AGREE WITH THESE CONDITIONS THEN PLEASE PRESS ESC NOW.";

                    dt[0].color = DT_COLOR_RED;
                    dt[0].x = 0;
                    dt[0].y = 0;
                    dt[1].text = null;
                    displaytext(dt, 1);

                    i = osd_read_key();
                    while (osd_key_pressed(i)) /* wait for key release */ {
                        osd_refresh();
                    }

                    if (i != OSD_KEY_ESC) {
                        	String cfgname;


				showcredits();	/* show the driver credits */

				osd_clearbitmap(Machine.scrbitmap);
				osd_update_display();

                                cfgname = sprintf("roms/%s/%s.cfg", new Object[]{gamename, gamename});
                                try {
                                    /* load input ports settings (keys, dip switches, and so on) */
                                    load_input_port_settings();
                                } catch (IOException ex) {
                                    Logger.getLogger(mame.class.getName()).log(Level.SEVERE, null, ex);
                                }

				/* we have to load the hi scores, but this will be done while */
				/* the game is running */
                                hiscoreloaded = 0;
                                hiscorename =sprintf("roms/%s/%s.hi", new Object[]{gamename, gamename});

				cpu_run();	/* run the emulation! */

                                if (drv.sh_stop != null) {
                                        drv.sh_stop.handler();
                                    }
                                    if (drv.vh_stop != null) {
                                        drv.vh_stop.handler();
                                    }
                        

				/* write hi scores to disk */
                                if ((hiscoreloaded != 0) && (gamedrv.hiscore_save != null)) {
                                    gamedrv.hiscore_save.handler();
                                }
                                try {
                                    /* save input ports settings */
                                    save_input_port_settings();
                                } catch (FileNotFoundException ex) {
                                    Logger.getLogger(mame.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(mame.class.getName()).log(Level.SEVERE, null, ex);
                                }
                    }                                      
                    res = 0;
                } else {
                   printf("Unable to start audio emulation\n", new Object[0]);
                }
            } else {
               printf("Unable to start video emulation\n", new Object[0]);
            }

            vh_close();
        } else {
           printf("Unable to initialize display\n", new Object[0]);
        }

        return res;
    }
}
