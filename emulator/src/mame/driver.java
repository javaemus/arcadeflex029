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
 *   commented drivers are the TODO drivers
 */
package mame;
import static mame.driverH.*;
import static drivers.docastle.*;
import static drivers.dowild.*;
import static drivers.pooyan.*;
import static drivers.scobra.*;
import static drivers.seicross.*;
import static drivers.blueprnt.*;
import static drivers.scramble.*;
import static drivers.pengo.*;
import static drivers.zaxxon.*;
import static drivers.rallyx.*;
import static drivers.mrdo.*;
import static drivers.ladybug.*;
import static drivers.amidar.*;
import static drivers.arabian.*;
import static drivers.timeplt.*;
import static drivers.jrpacman.*;
import static drivers.commando.*;
import static drivers.gberet.*;
import static drivers.carnival.*;
import static drivers.bombjack.*;
import static drivers.bagman.*;
import static drivers.cclimber.*;
import static drivers.ckongs.*;
import static drivers.congo.*;
import static drivers.mario.*;
import static drivers.popeye.*;
import static drivers.elevator.*;
import static drivers.junglek.*;
import static drivers.gyruss.*;
import static drivers.jumpbug.*;
import static drivers.mpatrol.*;
import static drivers.moonqsr.*;
import static drivers.mooncrst.*;
import static drivers.frogger.*;
import static drivers.galaxian.*;
import static drivers.panic.*;
import static drivers.invaders.*;
import static drivers.kungfum.*;
import static drivers.pacman.*;
import static drivers.digdug.*;
import static drivers.naughtyb.*;
import static drivers.locomotn.*;
import static drivers.wwestern.*;
import static drivers.galaga.*;
import static drivers.superpac.*;
import static drivers.vanguard.*;
import static drivers.venture.*;
import static drivers.mtrap.*;
import static drivers.pepper2.*;
import static drivers.nibbler.*;
import static drivers.phoenix.*;
import static drivers.btime.*;
import static drivers.kangaroo.*;
import static drivers.bosco.*;
import static drivers.yiear.*;
import static drivers.eggs.*;
import static drivers.sonson.*;
import static drivers.exedexes.*;
import static drivers.tutankhm.*;
import static drivers.mystston.*;
import static drivers.yard.*;
import static drivers.asteroid.*;
import static drivers.mappy.*;
import static drivers.missile.*;
import static drivers.xevious.*;
import static drivers.bwidow.*;
import static drivers.llander.*;
import static drivers.redbaron.*;
import static drivers.bzone.*;
import static drivers.sbasketb.*;
import static drivers.warlord.*;
import static drivers.espial.*;
import static drivers.bankp.*;
import static drivers.tp84.*;
import static drivers.mcr1.*;
import static drivers.mcr2.*;
import static drivers.mcr3.*;
import static drivers.omegrace.*;
import static drivers.dkong.*;
import static drivers.gng.*;
import static drivers.starforc.*;
import static drivers.vulgus.*;
import static drivers.warpwarp.*;
import static drivers.spacefb.*;
import static drivers.ccastles.*;
import static drivers.centiped.*;
import static drivers.milliped.*;
import static drivers.tempest.*;
import static drivers.williams.*;
import static drivers._1942.*;
import static drivers.qbert.*;
import static drivers.qbertqub.*;
import static drivers.mplanets.*;
import static drivers._3stooges.*;
import static drivers.krull.*;
import static drivers.reactor.*;
import static drivers.qix.*;
import static drivers.bublbobl.*;
import static drivers.wow.*;
import static drivers.rastan.*;
import static drivers.blktiger.*;
import static drivers.sidearms.*;
import static drivers.silkworm.*;
import static drivers.gunsmoke.*;
import static drivers.lwings.*;
import static drivers._1943.*;
import static drivers.system8.*;
import static drivers.rocnrope.*;

public class driver {
	public static GameDriver drivers[] =
	{
            /*WORKING GAMES*/
            mario_driver,
            ladybug_driver,
            snapjack_driver,
            cavenger_driver,
            pacman_driver,   
            pacmod_driver,
            npacmod_driver, //namcopac is npacmod in v0.36 romset
            pacmanjp_driver,
            hangly_driver,
            puckman_driver,
            piranha_driver,
            mspacman_driver,
            mspacatk_driver,
            crush2_driver, //crush is crush2 in v0.36 romset
            pengo_driver,
            penta_driver,
            gberet_driver,
            rushatck_driver,
            invaders_driver,
            earthinv_driver,
            spaceatt_driver,
            invrvnge_driver,
            invdpt2m_driver, //invdelux is  invdpt2m in v0.36 romset
            galxwars_driver,
            lrescue_driver,
            desterth_driver,
            arabian_driver,
            elevatob_driver, 
            sonson_driver,
            btime_driver,
            btimem_driver,//btimea is btimem in 0.36romset
            blueprnt_driver,
            digdug_driver,// digdugnm is digdug in v0.36 romset
            digdugat_driver,
            commando_driver,
            commandj_driver,
            yiear_driver,
            wwestern_driver,
            vanguard_driver,
            kangaroo_driver,
            jrpacman_driver,
            phoenix_driver,
            phoenixt_driver,
            phoenix3_driver,
            pleiads_driver,
            pooyan_driver,
            superpac_driver,
            seicross_driver,
            jumpbug_driver,
            jumpbugb_driver,//jbugsega is jumpbugb in 0.36 romset
            amidar_driver, /*amidarjp in v0.27 , amidar in v0.36*/
            amidaru_driver,/*amidar in v0.27 , amidaru in v0.36*/
            amigo_driver,
            turtles_driver,
            turpin_driver,
            kungfum_driver,
            kungfub2_driver, //kungfub is kungfub2 in v0.36 romset
            popeyebl_driver,
            nibbler_driver,
            gyruss_driver,
            frogger_driver,
            frogseg2_driver,//frogsega is frogseg2 in 0.36 romset
            eggs_driver,
            locomotn_driver,
            tutankhm_driver,
            mystston_driver,
            junglek_driver,
            jungleh_driver, //jhunt is jungleh in 0.36 romset 
            zaxxon_driver,
            panic_driver,
            panica_driver,
            congo_driver,
            rallyx_driver,
            cclimber_driver,
            cclimbrj_driver, /*ccjap is cclimbrj in 0.36 romset */
            ccboot_driver,
            exedexes_driver,
            venture_driver,
            mtrap_driver,
            mpatrol_driver,
            mranger_driver,
            naughtyc_driver, //naughtyb is naghtyc in v0.36 romset
            popflame_driver,
            mrdo_driver,
            mrdot_driver,
            mrlo_driver,
            mappy_driver,
            digdug2_driver,
            docastle_driver,
            docastl2_driver,
            dounicorn_driver,
            dowild_driver,
            dorunrun_driver,
            kickridr_driver,
            carnival_driver,
            bagman_driver,
            sbagman_driver,
            galaga_driver, //galaganm is galaga in 0.36romset
            galagamw_driver,//galaga is galagamw in 0.36romset
            galagab2_driver,//galagabl is galagab2 in 0.36romset
            gallag_driver,    
            mooncrst_driver,
            mooncrsg_driver, // mooncrsb is mooncrsg in v0.36 romset
            fantazia_driver,
            scobra_driver,
            scobras_driver, /*scobrak replaced with scobras since it doesn't exist in v0.36 romset*/
            scobrab_driver,
            losttomb_driver,
            anteater_driver,
            rescue_driver,                     
            atlantis_driver,
            theends_driver, /*this was actually theend in v0.27 but in romset 0.36 it was renamed to theends*/
            froggers_driver,   
            galaxian_driver,
            galmidw_driver,
            superg_driver,
            galapx_driver,
            galap1_driver,
            galap4_driver,
            galturbo_driver,
            pisces_driver,
            uniwars_driver,
            warofbug_driver,
            bombjack_driver,
            timeplt_driver,
            spaceplt_driver,        
            asteroid_driver,
            asteroi1_driver,//asteroi2 is asteroi1 in v0.36 romset
            astdelux_driver,
            astdelu1_driver,
            missile_driver,
            bwidow_driver,
            gravitar_driver,
            spacduel_driver,
            redbaron_driver, 
            espial_driver,
            bankp_driver,
            twotiger_driver, 
            domino_driver,  
            wacko_driver,   
            kroozr_driver,  
            tapper_driver, 
            dotron_driver, 
            timber_driver,  
            spyhunt_driver, 
            rampage_driver, 
            omegrace_driver,
            dkong_driver,
            dkongjp_driver,
            dkongjr_driver,
            dkong3_driver,
            gng_driver,
            diamond_driver,
            starforc_driver,
            robotron_driver,
            stargate_driver,
            defender_driver,
            blaster_driver, 
            c1942_driver,
            qbert_driver,
            qbertjp_driver,
            qbertqub_driver,
            krull_driver,
            mplanets_driver,
            stooges_driver,  
            qix_driver, 
            wow_driver,
            robby_driver,
            spacezap_driver,
            bublbobl_driver,
            boblbobl_driver,
            dkjrjp_driver, /*added to 0.29 */
            dkjrbl_driver, /*added to 0.29 */
            blktiger_driver, /*added to 0.29 */
            pepper2_driver,  /*fixed in 0.29 */         
            silkworm_driver, /*added to 0.29 */
            lwings_driver,
            sectionz_driver,
            c1943_driver,
            
            wbdeluxe_driver, /* hacky 0.31 driver*/
            
            /*PARTIAL WORKING GAMES*/
            
            //elevator_driver,
            //pacnpal_driver,
            //fantasy_driver,
            //yard_driver,
            //moonqsr_driver,
            //ckongs_driver,
            //bosco_driver,
            //sbasketb_driver,
            //kick_driver,      //orientation issue
            //solarfox_driver,  //orientation issue
            //tron_driver,      //orientation issue
            //shollow_driver, //orientation issue
            //journey_driver,   //orientation issue
            //destderb_driver, //weird controls (requires trakball)
            //vulgus_driver,
            //spacefb_driver,
            //joust_driver,
            //splat_driver,
            //gorf_driver,
            // sidearms_driver, /*added in 0.29 error in graphics */
            //gunsmoke_driver, /*added in 0.29 error in graphics */
            
            
            /*NOT WORKING GAMES*/
            
            //pacplus_driver,
            //frontlin_driver,
            //ckong_driver,
            //ckonga_driver,
            //ckongjeu_driver,
            //scramble_driver,
            //xevious_driver,
            //xeviousa_driver, //xeviousn is xeviousa in 0.36 romset
            //sxevious_driver,
            //llander_driver,
            //bzone_driver,
            //bzone2_driver,
            //warlord_driver,
            //tp84_driver,
            //ccastles_driver,     //works a bit better than before...         
            //tempest_driver,
            //centiped_driver,
            //milliped_driver,
            //warpwarp_driver,
            //sinistar_driver,
            //bubbles_driver,
            //reactor_driver,
            //seawolf2_driver,//seawolf is seawolf2 in v0.36 romset
            //radarscp_driver, /*new in 0.29 - not working */
           
            //hunchy_driver,  /*new in 0.29 - not working */
            
            
            /*NON EXIST GAMES*/
            
            //pengoa_driver,  //we don't support that since it doesn't exist in v0.36 romset
            //galnamco_driver, //that game seems not to exist in 0.36 romset
            //japirem_driver,//that game seems not to exist in 0.36 romset
            //gngcross_driver,//doesn't seem to exist in 0.36 romset
            
            
            /*NOT YET IMPLEMENTED GAMES*/   
                          
            /*will not implemented for this version*/
            
             //starwars_driver,         
             //spacfury_driver,
             //zektor_driver,
             //tacscan_driver,
             //elim2_driver,
             //startrek_driver,
             rastan_driver,
            null	/* end of array */
        };
}
