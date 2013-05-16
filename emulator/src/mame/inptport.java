/*
 * ported to v0.29
 * ported to v0.28
 *
 *   readtrakport is partially implemented
 *   saving loading configs at the old way , doesn't save trak nor joystick settings
 *
 */
package mame;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static arcadeflex.osdepend.*;
import static mame.cpuintrf.*;

public class inptport {
    static final int MAX_INPUT_PORTS=16;//from inptport.h
    
    static int[] input_port_value=new int[MAX_INPUT_PORTS];
    static int[] input_vblank=new int[MAX_INPUT_PORTS];
    
    /* Assuming a maxium of one analog input device per port BW 101297 */
    static NewInputPort[] input_analog=new NewInputPort[MAX_INPUT_PORTS];
    static int[] input_analog_value=new int[MAX_INPUT_PORTS];
    static int[] input_analog_delta=new int[MAX_INPUT_PORTS];
   
    static int newgame = 1; /* LBO */
    /***************************************************************************

      Obsolete functions which will eventually be removed

    ***************************************************************************/
    static void old_load_input_port_settings()
    {
            FILE f;
            char buf[] =new char[100];
            float temp;
            int i,j;
            int len,incount,keycount,trakcount;


            incount = 0;
            while (Machine.gamedrv.input_ports[incount].default_value != -1) incount++;

            keycount = 0;
            while (Machine.gamedrv.keysettings[keycount].num != -1) keycount++;

                                    trakcount = 0;
                                    if (Machine.gamedrv.trak_ports!=null)
                                    {
                                            while (Machine.gamedrv.trak_ports[trakcount].axis != -1) trakcount++;
                                    }

                                    /* find the configuration file */
                                    if ((f = fopen("cfg/"+Machine.gamedrv.name+".cfg","rb")) != null)
                                    {
                                      for (j=0; j < 4; j++)
                                      {
                                             if ((len = fread(buf,1,4,f)) == 4)
                                             {
                                                    len = buf[3];
                                                    buf[3] = '\0';
                                                    if (stricmp(buf,"dsw") == 0) 
                                                    {
                                                      if ((len == incount) && (fread(buf,1,incount,f) == incount))
                                                      {
                                                              for (i = 0;i < incount;i++)
                                                                      Machine.gamedrv.input_ports[i].default_value = buf[i];
                                                      }
                                                    } 
                                                    else if (stricmp(buf,"key") == 0) {
                                                      if ((len == keycount) && (fread(buf,1,keycount,f) == keycount))
                                                      {
                                                              for (i = 0;i < keycount;i++)
                                                                      Machine.gamedrv.input_ports[ Machine.gamedrv.keysettings[i].num ].keyboard[ Machine.gamedrv.keysettings[i].mask ] = buf[i];
                                                      }
                                                    } else if (stricmp(buf,"joy") == 0) {
                                    /*TODO:joystic    if ((len == keycount) && (fread(buf,1,keycount,f) == keycount))
                                                      {
                                                              for (i = 0;i < keycount;i++)
                                                                      Machine.gamedrv.input_ports[ Machine.gamedrv.keysettings[i].num ].joystick[ Machine.gamedrv.keysettings[i].mask ] = ((unsigned char)buf[i]);
                                                       }*/
                                                    } 
                                                    else if (stricmp(buf,"trk") == 0) 
                                                    {
                                         /*TODO:TRAK     /*if ((len == trakcount) && (fread(buf,sizeof(float),trakcount,f) == trakcount))
                                                          {
                                                                  for (i = 0;i < trakcount;i++) 
                                                                  {
                                                                         memcpy(&temp, &buf[i*sizeof(float)], sizeof(float));
                                                                          Machine.gamedrv.trak_ports[i].scale = temp;
                                                                  }
                                                          }*/
                                                      }
                                             }
                                      }
                                      fclose(f);
                                    }
    }
    static void old_save_input_port_settings()
    {
            FILE f;
            char buf[]=new char[100];
            int i;
            int incount,keycount,trakcount;


    incount = 0;
    while (Machine.gamedrv.input_ports[incount].default_value != -1) incount++;

    keycount = 0;
    while (Machine.gamedrv.keysettings[keycount].num != -1) keycount++;

                                    trakcount = 0;
                                    if (Machine.gamedrv.trak_ports!=null)
                                    {
                                            while (Machine.gamedrv.trak_ports[trakcount].axis != -1) trakcount++;
                                    }

                                    /* write the configuration file */
                                    if ((f = fopen("cfg/"+Machine.gamedrv.name+".cfg","wb")) != null)
                                    {
                                                    sprintf(buf, "dsw ", new Object[0]);
                                                    buf[3] = (char)incount;
                                                    fwrite(buf,1,4,f);
            /* use buf as temporary buffer */
            for (i = 0;i < incount;i++)
                    buf[i] = (char)Machine.gamedrv.input_ports[i].default_value;
            fwrite(buf,1,incount,f);

                                                    sprintf(buf, "key ", new Object[0]);
                                                    buf[3] = (char)keycount;
                                                    fwrite(buf,1,4,f);
            /* use buf as temporary buffer */
            for (i = 0;i < keycount;i++)
                    buf[i] = (char)Machine.gamedrv.input_ports[ Machine.gamedrv.keysettings[i].num ].keyboard[ Machine.gamedrv.keysettings[i].mask ];
            fwrite(buf,1,keycount,f);

                                               /* JOY TODO!   sprintf(buf, "joy ", new Object[0]);
                                                    buf[3] = keycount;
                                                    fwrite(buf,1,4,f);*/
            /* use buf as temporary buffer */
          /*  for (i = 0;i < keycount;i++)
                    buf[i] = Machine.gamedrv.input_ports[ Machine.gamedrv.keysettings[i].num ].joystick[ Machine.gamedrv.keysettings[i].mask ];
            fwrite(buf,1,keycount,f);*/

                                           /*TRAK TODO!         sprintf(buf, "trk ");
                                                    buf[3] = trakcount;
                                                    fwrite(buf,1,4,f);*/
            /* use buf as temporary buffer */
            /*for (i = 0;i < trakcount;i++)
                                                            memcpy(&buf[i*sizeof(float)], &Machine.gamedrv.trak_ports[i].scale, sizeof(float));
            fwrite(buf,sizeof(float),trakcount,f);*/
                                                    fclose(f);
                                    }
    }
    static void old_update_input_ports()
    {
            int port;


            /* scan all the input ports */
            port = 0;
            while (Machine.gamedrv.input_ports[port].default_value != -1)
            {
                    int res, i;
                    InputPort in = Machine.gamedrv.input_ports[port];

                    res = in.default_value;
                    input_vblank[port] = 0;

                    for (i = 7;i >= 0;i--)
                    {
                            int c;


                            c = in.keyboard[i];
                            if (c == IPB_VBLANK)
                            {
                                    res ^= (1 << i);
                                    input_vblank[port] ^= (1 << i);
                            }
                            else
                            {
                                     if ((c !=0)&& osd_key_pressed(c))
                                            res ^= (1 << i);
                                    /*else //JOYSTIK TODO!
                                    {
                                            c = in.joystick[i];
                                            if (c && osd_joy_pressed(c))
                                                    res ^= (1 << i);
                                    }*/
                            }
                    }

                    input_port_value[port] = res;
                    port++;
            }
    }
        public static int readtrakport(int port)
        {
              int axis;
              int read;
              
              TrakPort in = Machine.gamedrv.trak_ports[port];
              axis = in.axis;

              read = osd_trak_read(axis);
              

              if(in.conversion!=null) {
                  return in.conversion.handler((int)(read*in.scale));//TODO probably correct
               // return((*in->conversion)(read*in->scale));
              }

              return (int)(read * in.scale);
        }

        public static ReadHandlerPtr input_trak_0_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readtrakport(0);}};
        public static ReadHandlerPtr input_trak_1_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readtrakport(1);}};
        public static ReadHandlerPtr input_trak_2_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readtrakport(2);}};
        public static ReadHandlerPtr input_trak_3_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readtrakport(3);}};
        /***************************************************************************

              End of obsolete functions

        ***************************************************************************/
          
          public static class ipd
          {
            public ipd(int type,String name,int keyboard,int joystick)
            {
                this.type=type;
                this.name=name;
                this.keyboard=keyboard;
                this.joystick=joystick;
            }
            int type;
            String name;
            int keyboard;
            int joystick;
          };
          static ipd inputport_defaults[] = 
          {
                new ipd( IPT_JOYSTICK_UP,        "Up",              OSD_KEY_UP,     0 /*OSD_JOY_UP*/ ),
                new ipd( IPT_JOYSTICK_DOWN,      "Down",            OSD_KEY_DOWN,     0 /*OSD_JOY_DOWN*/ ),
                new ipd( IPT_JOYSTICK_LEFT,      "Left",            OSD_KEY_LEFT,     0 /*OSD_JOY_LEFT*/ ),
                new ipd( IPT_JOYSTICK_RIGHT,     "Right",           OSD_KEY_RIGHT,    0 /*OSD_JOY_RIGHT*/ ),
                new ipd( IPT_JOYSTICK_UP    | IPF_PLAYER2, "2 Up",            OSD_KEY_R,       0 ),
                new ipd( IPT_JOYSTICK_DOWN  | IPF_PLAYER2, "2 Down",          OSD_KEY_F,       0 ),
                new ipd( IPT_JOYSTICK_LEFT  | IPF_PLAYER2, "2 Left",          OSD_KEY_D,       0 ),
                new ipd( IPT_JOYSTICK_RIGHT | IPF_PLAYER2, "2 Right",         OSD_KEY_G,       0 ),
                new ipd( IPT_JOYSTICK_UP    | IPF_PLAYER3, "3 Up",            0,               0 ),
                new ipd( IPT_JOYSTICK_DOWN  | IPF_PLAYER3, "3 Down",          0,               0 ),
                new ipd( IPT_JOYSTICK_LEFT  | IPF_PLAYER3, "3 Left",          0,               0 ),
                new ipd( IPT_JOYSTICK_RIGHT | IPF_PLAYER3, "3 Right",         0,               0 ),
                new ipd( IPT_JOYSTICK_UP    | IPF_PLAYER4, "4 Up",            0,               0 ),
                new ipd( IPT_JOYSTICK_DOWN  | IPF_PLAYER4, "4 Down",          0,               0 ),
                new ipd( IPT_JOYSTICK_LEFT  | IPF_PLAYER4, "4 Left",          0,               0 ),
                new ipd( IPT_JOYSTICK_RIGHT | IPF_PLAYER4, "4 Right",         0,               0 ),
                new ipd( IPT_JOYSTICKRIGHT_UP,    "Right/Up",        OSD_KEY_I,        0 /*OSD_JOY_FIRE2*/ ),
                new ipd( IPT_JOYSTICKRIGHT_DOWN,  "Right/Down",      OSD_KEY_K,        0 /*OSD_JOY_FIRE3*/ ),
                new ipd( IPT_JOYSTICKRIGHT_LEFT,  "Right/Left",      OSD_KEY_J,        0 /*OSD_JOY_FIRE1*/ ),
                new ipd( IPT_JOYSTICKRIGHT_RIGHT, "Right/Right",     OSD_KEY_L,        0 /*OSD_JOY_FIRE4*/ ),
                new ipd( IPT_JOYSTICKLEFT_UP,     "Left/Up",         OSD_KEY_E,        0 /*OSD_JOY_UP*/ ),
                new ipd( IPT_JOYSTICKLEFT_DOWN,   "Left/Down",       OSD_KEY_D,        0 /*OSD_JOY_DOWN*/ ),
                new ipd( IPT_JOYSTICKLEFT_LEFT,   "Left/Left",       OSD_KEY_S,        0 /*OSD_JOY_LEFT*/ ),
                new ipd( IPT_JOYSTICKLEFT_RIGHT,  "Left/Right",      OSD_KEY_F,        0 /*OSD_JOY_RIGHT*/ ),
                new ipd( IPT_BUTTON1,             "Button 1",        OSD_KEY_CONTROL,  0 /*OSD_JOY_FIRE1*/ ),  //TODO OSD_KEY_LCONTROL 
                new ipd( IPT_BUTTON2,             "Button 2",        OSD_KEY_ALT,      0 /*OSD_JOY_FIRE2*/ ),
                new ipd( IPT_BUTTON3,             "Button 3",        OSD_KEY_SPACE,    0 /*OSD_JOY_FIRE3*/ ),
                new ipd( IPT_BUTTON4,             "Button 4",        OSD_KEY_LSHIFT,   0 /*OSD_JOY_FIRE4*/ ),
                new ipd( IPT_BUTTON5,             "Button 5",        OSD_KEY_Z,       0 ),
                new ipd( IPT_BUTTON1 | IPF_PLAYER2, "2 Button 1",        OSD_KEY_A,       0 ),
                new ipd( IPT_BUTTON2 | IPF_PLAYER2, "2 Button 2",        OSD_KEY_S,       0 ),
                new ipd( IPT_BUTTON3 | IPF_PLAYER2, "2 Button 3",        OSD_KEY_Q,       0 ),
                new ipd( IPT_BUTTON4 | IPF_PLAYER2, "2 Button 4",        OSD_KEY_W,       0 ),
                new ipd( IPT_BUTTON1 | IPF_PLAYER3, "3 Button 1",        0,               0 ),
                new ipd( IPT_BUTTON2 | IPF_PLAYER3, "3 Button 2",        0,               0 ),
                new ipd( IPT_BUTTON3 | IPF_PLAYER3, "3 Button 3",        0,               0 ),
                new ipd( IPT_BUTTON4 | IPF_PLAYER3, "3 Button 4",        0,               0 ),
                new ipd( IPT_BUTTON1 | IPF_PLAYER4, "4 Button 1",        0,               0 ),
                new ipd( IPT_BUTTON2 | IPF_PLAYER4, "4 Button 2",        0,               0 ),
                new ipd( IPT_BUTTON3 | IPF_PLAYER4, "4 Button 3",        0,               0 ),
                new ipd( IPT_BUTTON4 | IPF_PLAYER4, "4 Button 4",        0,               0 ),
                new ipd( IPT_COIN1,               "Coin A",          OSD_KEY_3,       IP_JOY_NONE ),
                new ipd( IPT_COIN2,               "Coin B",          OSD_KEY_4,       IP_JOY_NONE ),
                new ipd( IPT_COIN3,               "Coin C",          OSD_KEY_5,       IP_JOY_NONE ),
                new ipd( IPT_COIN4,               "Coin D",          OSD_KEY_6,       IP_JOY_NONE ),
                new ipd( IPT_TILT,                "Tilt",            OSD_KEY_T,       IP_JOY_NONE ),
                new ipd( IPT_START1,              "1 Player Start",  OSD_KEY_1,       IP_JOY_NONE ),
                new ipd( IPT_START2,              "2 Players Start", OSD_KEY_2,       IP_JOY_NONE ),
                new ipd( IPT_UNKNOWN,             "UNKNOWN",         IP_KEY_NONE,     IP_JOY_NONE ),
                new ipd( IPT_END,                 null,              IP_KEY_NONE,     IP_JOY_NONE ),
          };
            public static String default_name(NewInputPort in)
            {
                    int i;

                    if(in.name==null) return null;
                    if (in.name.compareTo(IP_NAME_DEFAULT) != 0) return in.name;

                    i = 0;
                    while (inputport_defaults[i].type != IPT_END &&
                                    inputport_defaults[i].type != (in.type & (~IPF_MASK | IPF_PLAYERMASK)))
                            i++;

                    return inputport_defaults[i].name;
            }
            public static int default_key(NewInputPort in)
            {
                    int i;

                    //if (in->keyboard == IP_KEY_PREVIOUS) in--;  //TODO required???
                    if (in.keyboard != IP_KEY_DEFAULT) return in.keyboard;

                    i = 0;
                    while (inputport_defaults[i].type != IPT_END &&
                                    inputport_defaults[i].type != (in.type & (~IPF_MASK | IPF_PLAYERMASK)))
                            i++;

                    return inputport_defaults[i].keyboard;
            }
            /* NS, LBO 100397, BW 101297 */
            static void update_analog_port(int port)
            {
                    NewInputPort in;
                    int current, delta, scaled_delta, type, sensitivity, clip, min, max;
                    int axis, inckey, deckey, is_stick;

                    /* get input definition */
                    in=input_analog[port];
                    type=(in.type & ~IPF_MASK);

                    switch (type) {
                            case IPT_DIAL:
                                    axis = X_AXIS;
                                    deckey = OSD_KEY_Z;
                                    inckey = OSD_KEY_X;
                                    is_stick = 0;
                                    break;
                            case IPT_TRACKBALL_X:
                                    axis = X_AXIS;
                                    deckey = OSD_KEY_LEFT;
                                    inckey = OSD_KEY_RIGHT;
                                    is_stick = 0;
                                    break;
                            case IPT_TRACKBALL_Y:
                                    axis = Y_AXIS;
                                    deckey = OSD_KEY_UP;
                                    inckey = OSD_KEY_DOWN;
                                    is_stick = 0;
                                    break;
                            case IPT_AD_STICK_X:
                                    axis = X_AXIS;
                                    deckey = OSD_KEY_LEFT;
                                    inckey = OSD_KEY_RIGHT;
                                    is_stick = 1;
                                    break;
                            case IPT_AD_STICK_Y:
                                    axis = Y_AXIS;
                                    deckey = OSD_KEY_UP;
                                    inckey = OSD_KEY_DOWN;
                                    is_stick = 1;
                                    break;
                            default:
                                    /* Use some defaults to prevent crash */
                                    axis = X_AXIS;
                                    deckey = OSD_KEY_Z;
                                    inckey = OSD_KEY_Y;
                                    is_stick = 0;
                                    if (errorlog!=null)
                                            fprintf (errorlog,"Oops, polling non analog device in update_analog_port()????\n");
                    }


                    if (newgame!=0 || (((in.type & IPF_CENTER)!=0) && (is_stick==0)))
                            input_analog_value[port] = in.default_value;

                    current = input_analog_value[port];

                    delta = osd_trak_read(axis);

                    if (osd_key_pressed(deckey))
                    {
                            delta -= 4;
                    }
                    if (osd_key_pressed(inckey))
                    {
                            delta += 4;     
                    }

                    sensitivity = in.arg & 0x000000ff;
                    clip = (in.arg & 0x0000ff00) >> 8;

                    /* Internally, we use 16=2^4 as default sensivity. 32 is twice as */
                    /* sensitive, 8 half, and so on. The macro in driver.h translates */
                    /* this to the old "percent" semantics, so there's no need to     */
                    /* change the drivers. 101297 BW */

                    scaled_delta = (delta * sensitivity) / 16;

                    if (clip != 0) {
                            if (scaled_delta < -clip)
                                    scaled_delta = -clip;
                            else if (scaled_delta > clip)
                                    scaled_delta = clip;
                    }

                    if ((in.type & IPF_REVERSE)!=0) scaled_delta = -scaled_delta;

                    if ((is_stick)!=0)
                    {
                            if (axis == Y_AXIS) scaled_delta = -scaled_delta;
                            if ((delta == 0) && ((in.type & IPF_CENTER)!=0))
                            {
                                    if ((current+scaled_delta)>in.default_value)
                                    scaled_delta-=1;
                                    if ((current+scaled_delta)<in.default_value)
                                    scaled_delta+=1;
                            }

                            min = (in.arg & 0x00ff0000) >> 16;
                            max = (in.arg & 0xff000000) >> 24;
                            if (current+scaled_delta <= min)
                                    scaled_delta=min-current;
                            if (current+scaled_delta >= max)
                                    scaled_delta=max-current;
                    }

                    current += scaled_delta;
                    current &= in.mask;

                    input_analog_value[port]=current;

                    input_port_value[port] &= ~in.mask;
                    input_port_value[port] |= current;
            }

            static void update_analog_ports()
            {
                    int port;

                    for (port = 0;port < MAX_INPUT_PORTS;port++)
                    {
                            if (input_analog[port]!=null)
                                    update_analog_port(port);
                    }
            }
          static final int MAX_INPUT_BITS= 1024;
          static final int MAX_JOYSTICKS= 3;
          static final int MAX_PLAYERS= 4;
          static int impulsecount[]=new int[MAX_INPUT_BITS];
          static int waspressed[]=new int[MAX_INPUT_BITS];
          static int trackball[]=new int[MAX_INPUT_BITS];
         public static void update_input_ports()
         { 
            int i,port,ib;
            NewInputPort[] in=Machine.input_ports;
            int joystick[][] =new int[MAX_JOYSTICKS*MAX_PLAYERS][4];
            if (Machine.input_ports == null)
            {
                    old_update_input_ports();
                    return;
            }
            /* clear all the values before proceeding */
            for (port = 0;port < MAX_INPUT_PORTS;port++)
            {
                    input_port_value[port] = 0;
                    input_vblank[port] = 0;
                    input_analog[port] = null;
                    input_analog_delta[port] = 0;
            }

            for (i = 0;i < 4*MAX_JOYSTICKS*MAX_PLAYERS;i++)
                    joystick[i/4][i%4] = 0;
            
            int in_ptr=0;
            
            //in[in_ptr] = Machine.input_ports[in_ptr];

            if (in[in_ptr].type == IPT_END) return; 	/* nothing to do */

            /* make sure the InputPort definition is correct */
            if (in[in_ptr].type != IPT_PORT)
            {
                    if (errorlog!=null) fprintf(errorlog,"Error in InputPort definition: expecting PORT_START\n");
                    System.out.println("Error in InputPort definition: expecting PORT_START\n");
                    return;
            }
            else
            {
                in_ptr++;
            }
                

            /* scan all the input ports */
            port = 0;
            ib = 0;
                int save_ptr=0;
            	while (in[in_ptr].type != IPT_END && port < MAX_INPUT_PORTS)
                {
                        //struct NewInputPort *start;
                        NewInputPort[] start=in;
                        /* first of all, scan the whole input port definition and build the */
                        /* default value. I must do it before checking for input because otherwise */
                        /* multiple keys associated with the same input bit wouldn't work (the bit */
                        /* would be reset to its default value by the second entry, regardless if */
                        /* the key associated with the first entry was pressed) */
                        //start[in_ptr] = in[in_ptr];
                        save_ptr=in_ptr;//save ptr so we can use it later
                        while (in[in_ptr].type != IPT_END && in[in_ptr].type != IPT_PORT)
                        {
                                if ((in[in_ptr].type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING)	/* skip dipswitch definitions */
                                {
                                        input_port_value[port] =
                                                        (input_port_value[port] & ~in[in_ptr].mask) | (in[in_ptr].default_value & in[in_ptr].mask);
                                }

                                in_ptr++;
                        }

                        /* now get back to the beginning of the input port and check the input bits. */
                        in_ptr=save_ptr;//go back to the beginning;
                        //in[in_ptr] = start[in_ptr];
                        while (in[in_ptr].type != IPT_END && in[in_ptr].type != IPT_PORT)
                        {
                                if ((in[in_ptr].type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING &&	/* skip dipswitch definitions */
                                               (in[in_ptr].type & IPF_UNUSED) == 0)	/* skip unused bits */
                                {
                                        if ((in[in_ptr].type & ~IPF_MASK) == IPT_VBLANK)
                                        {
                                                input_vblank[port] ^= in[in_ptr].mask;
                                                input_port_value[port] ^= in[in_ptr].mask;
                                        }
                                        else if (((in[in_ptr].type & ~IPF_MASK) == IPT_DIAL)
					|| ((in[in_ptr].type & ~IPF_MASK) == IPT_TRACKBALL_X)
					|| ((in[in_ptr].type & ~IPF_MASK) == IPT_TRACKBALL_Y)
					|| ((in[in_ptr].type& ~IPF_MASK) == IPT_AD_STICK_X)
					|| ((in[in_ptr].type & ~IPF_MASK) == IPT_AD_STICK_Y))
					{
						input_analog[port]=in[in_ptr];
						update_analog_port(port);
					}
                                        else
                                        {
                                                int key,joy;


                                                key = default_key(in[in_ptr]);
                                                //joy = default_joy(in[in_ptr]);

                                                if ((key != 0 && key != IP_KEY_NONE && osd_key_pressed(key))/* ||
                                                                (joy != 0 && joy != IP_JOY_NONE && osd_joy_pressed(joy))*/)
                                                {
                                                        if ((in[in_ptr].type & IPF_IMPULSE)!=0)
                                                        {
                                                            if (errorlog!=null && in[in_ptr].arg == 0)
                                                                    fprintf(errorlog,"error in input port definition: IPF_IMPULSE with length = 0\n");
                                                                if (waspressed[ib] == 0)
                                                                        impulsecount[ib] = in[in_ptr].arg;
                                                                        /* the input bit will be toggled later */
                                                        }
                                                        else if ((in[in_ptr].type & IPF_TOGGLE)!=0)
                                                        {
                                                                if (waspressed[ib] == 0)
                                                                {
                                                                        in[in_ptr].default_value ^= in[in_ptr].mask;
                                                                        input_port_value[port] ^= in[in_ptr].mask;
                                                                }
                                                        }
                                                        else if ((in[in_ptr].type & ~IPF_MASK) >= IPT_JOYSTICK_UP &&
                                                                        (in[in_ptr].type & ~IPF_MASK) <= IPT_JOYSTICKLEFT_RIGHT)
                                                        {
                                                                int joynum,joydir,mask,player;


                                                                player = 0;
                                                                if ((in[in_ptr].type & IPF_PLAYERMASK) == IPF_PLAYER2) player = 1;
                                                                else if ((in[in_ptr].type & IPF_PLAYERMASK) == IPF_PLAYER3) player = 2;
                                                                else if ((in[in_ptr].type & IPF_PLAYERMASK) == IPF_PLAYER4) player = 3;

                                                                joynum = player * MAX_JOYSTICKS +
                                                                                ((in[in_ptr].type & ~IPF_MASK) - IPT_JOYSTICK_UP) / 4;
                                                                joydir = ((in[in_ptr].type & ~IPF_MASK) - IPT_JOYSTICK_UP) % 4;

                                                                mask = in[in_ptr].mask;

                                                                /* avoid movement in two opposite directions */
                                                                if (joystick[joynum][joydir ^ 1] != 0)
                                                                        mask = 0;

                                                                if ((in[in_ptr].type & IPF_4WAY)!=0)
                                                                {
                                                                        int dir;


                                                                        /* avoid diagonal movements */
                                                                        for (dir = 0;dir < 4;dir++)
                                                                        {
                                                                                if (joystick[joynum][dir] != 0)
                                                                                        mask = 0;
                                                                        }
                                                                }

                                                                joystick[joynum][joydir] = 1;

                                                                input_port_value[port] ^= mask;
                                                        }
                                                        else
                                                                input_port_value[port] ^= in[in_ptr].mask;

                                                        waspressed[ib] = 1;
                                                }
                                                else
                                                        waspressed[ib] = 0;

                                                if (((in[in_ptr].type & IPF_IMPULSE)!=0) && impulsecount[ib] > 0)
                                                {
                                                        impulsecount[ib]--;
                                                        waspressed[ib] = 1;
                                                        input_port_value[port] ^= in[in_ptr].mask;
                                                }
                                        }
                                }

                                in_ptr++;
                                ib++;
                        }

                        port++;
                        if (in[in_ptr].type == IPT_PORT) in_ptr++;             
                }   
                newgame = 0; /* LBO */
         }
        //load and save functions have been rewritten using mostly java style
        public static void load_input_port_settings() throws IOException
        {
            if (Machine.input_ports == null)
            {
             old_load_input_port_settings();
             return;
            }
                File file = new File("cfg/"+Machine.gamedrv.name+".cfg");
                if(file.exists())
                {
                        if(file.length()==0) return;
                        RandomAccessFile raf=new RandomAccessFile(file, "r");
                        NewInputPort[] in=Machine.gamedrv.new_input_ports;
                        int total;
                        int savedtotal;
                        int in_ptr=0;
                        
                        /* calculate the size of the array */
                        total = 0;
                        while (in[in_ptr].type != IPT_END)
                        {
                                total++;
                                in_ptr++;
                        }
                        /* read header */
                        char header[] = new char[8];
            
                        for(int i=0; i<8; i++)
                        {
                            header[i]=(char)(raf.readByte() & 0xff);
                        }
                        if (memcmp(header,new char[]{'M','A','M','E','C','F','G','\0'},8) != 0)
                        {
                            System.out.println("Header in config file is invalid");
                             return;	/* header invalid */
                        }
                        savedtotal=raf.readInt();
                        if (total != savedtotal)
                        {
                            System.out.println("total size is not the same as savedtotal");
                             return;	/* different size */
                        }
                        in_ptr=0;
                        in=null;
                        /* read the original settings and compare them with the ones defined in the driver */
                        in = Machine.gamedrv.new_input_ports;         
                        while (in[in_ptr].type != IPT_END)
                        {
                            NewInputPort saved[]=new NewInputPort[1];
                            saved[0]=new NewInputPort();
                            
                            //readip
                            saved[0].mask = raf.readInt();
                            saved[0].default_value = raf.readInt();
                            saved[0].keyboard = raf.readInt();
                            saved[0].joystick = raf.readInt();
                            saved[0].arg = raf.readInt();
                            
                                //System.out.println("-------in_ptr---------------- "+ in_ptr);
                                //System.out.println("gamedrv : mask=" + in[in_ptr].mask + " default=" + in[in_ptr].default_value + " key=" + in[in_ptr].keyboard + " joy=" + in[in_ptr].joystick + " arg=" + in[in_ptr].arg);
                                //System.out.println("saved   : mask=" + saved[0].mask + " default=" + saved[0].default_value + " key=" + saved[0].keyboard + " joy=" + saved[0].joystick + " arg=" + saved[0].arg);
                                //System.out.println("-------END OF in_ptr---------------- "+ in_ptr);
                                if (in[in_ptr].mask != saved[0].mask ||
                                                in[in_ptr].default_value != saved[0].default_value ||
                                                in[in_ptr].keyboard != saved[0].keyboard ||
                                                in[in_ptr].joystick != saved[0].joystick ||
                                                in[in_ptr].arg != saved[0].arg)
                                        {
                                          System.out.println("-------NOT MACHED---------------- ");    
                                          System.out.println("gamedrv : mask=" + in[in_ptr].mask + " default=" + in[in_ptr].default_value + " key=" + in[in_ptr].keyboard + " joy=" + in[in_ptr].joystick + " arg=" + in[in_ptr].arg);
                                          System.out.println("saved   : mask=" + saved[0].mask + " default=" + saved[0].default_value + " key=" + saved[0].keyboard + " joy=" + saved[0].joystick + " arg=" + saved[0].arg);
                                          System.out.println("inptport:the default values are different");  
                                          return;	/* the default values are different */
                                        }

                                in_ptr++;
                        }
                        in_ptr=0;
                        in=null;
                        in = Machine.input_ports;
                        newgame = 1; /* LBO */      
                        while (in[in_ptr].type != IPT_END)
                        {
                                in[in_ptr].mask = raf.readInt();
                                in[in_ptr].default_value = raf.readInt();
                                in[in_ptr].keyboard = raf.readInt();
                                in[in_ptr].joystick = raf.readInt();
                                in[in_ptr].arg = raf.readInt();

                                in_ptr++;
                        }
                        
                        raf.close();
                }
                
 
        }
        public static void save_input_port_settings() throws FileNotFoundException, IOException
        {
            if (Machine.input_ports == null)
            {
                old_save_input_port_settings();
                return;
            }
                File file = new File("cfg/"+Machine.gamedrv.name+".cfg");
                RandomAccessFile raf=new RandomAccessFile(file, "rw");
                int int_ptr=0;
            	if (raf != null)
                {
                        
                        int total;
                        NewInputPort[] in=Machine.gamedrv.new_input_ports;


                        /* calculate the size of the array */
                         total = 0;
                        while (in[int_ptr].type != IPT_END)
                        {
                                total++;
                                int_ptr++;
                        }
                        /* write header */
                        char header[]=new char[]{'M','A','M','E','C','F','G','\0'};
                        for(int i=0; i<8; i++)
                        {
                            raf.writeByte(header[i]);
                        }
                        
                       /* write array size */
                         raf.writeInt(total);
                        /* write the original settings as defined in the driver */
                        int_ptr=0;
                        in=null;
                        in= Machine.gamedrv.new_input_ports;
                        //in[int_ptr] = Machine.gamedrv.new_input_ports[int_ptr];
                        while (in[int_ptr].type != IPT_END)
                        {                       
                                raf.writeInt(in[int_ptr].mask);
                                raf.writeInt(in[int_ptr].default_value);
                                raf.writeInt(in[int_ptr].keyboard);
                                raf.writeInt(in[int_ptr].joystick);
                                raf.writeInt(in[int_ptr].arg);                      
                                int_ptr++;
                        }
                        /* write the current settings */
                         int_ptr=0;
                        in=null;
                        in= Machine.input_ports;
                        //in[int_ptr] = Machine.input_ports[int_ptr];
                        while (in[int_ptr].type != IPT_END)
                        { 
                                raf.writeInt(in[int_ptr].mask);
                                raf.writeInt(in[int_ptr].default_value);
                                raf.writeInt(in[int_ptr].keyboard);
                                raf.writeInt(in[int_ptr].joystick);
                                raf.writeInt(in[int_ptr].arg);       
                                int_ptr++;
                        }

                        raf.close();
                }
        }
        public static int readinputport(int port)
        {
                if (input_vblank[port]!=0)
                {
                        /* I'm not yet sure about how long the vertical blanking should last, */
                        /* I think it should be about 1/12th of the frame. */
                        if (cpu_getfcount() < cpu_getfperiod() * 11 / 12)
                        {
                                input_port_value[port] ^= input_vblank[port];
                                input_vblank[port] = 0;
                        }
                }

                return input_port_value[port];
        }
      public static ReadHandlerPtr input_port_0_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readinputport(0);}};
      public static ReadHandlerPtr input_port_1_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readinputport(1);}};
      public static ReadHandlerPtr input_port_2_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readinputport(2);}};
      public static ReadHandlerPtr input_port_3_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readinputport(3);}};
      public static ReadHandlerPtr input_port_4_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readinputport(4);}};
      public static ReadHandlerPtr input_port_5_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readinputport(5);}};
      public static ReadHandlerPtr input_port_6_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readinputport(6);}};
      public static ReadHandlerPtr input_port_7_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readinputport(7);}};
      public static ReadHandlerPtr input_port_8_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readinputport(8);}};
      public static ReadHandlerPtr input_port_9_r = new ReadHandlerPtr() { public int handler(int paramInt) {return readinputport(9);}};
      public static ReadHandlerPtr input_port_10_r = new ReadHandlerPtr(){ public int handler(int paramInt) {return readinputport(10);}};
      public static ReadHandlerPtr input_port_11_r = new ReadHandlerPtr(){ public int handler(int paramInt) {return readinputport(11);}};
      public static ReadHandlerPtr input_port_12_r = new ReadHandlerPtr(){ public int handler(int paramInt) {return readinputport(12);}};
      public static ReadHandlerPtr input_port_13_r = new ReadHandlerPtr(){ public int handler(int paramInt) {return readinputport(13);}};
      public static ReadHandlerPtr input_port_14_r = new ReadHandlerPtr(){ public int handler(int paramInt) {return readinputport(14);}};
      public static ReadHandlerPtr input_port_15_r = new ReadHandlerPtr(){ public int handler(int paramInt) {return readinputport(15);}};
                                                
}
