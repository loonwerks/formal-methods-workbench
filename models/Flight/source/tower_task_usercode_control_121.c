/* This file has been autogenerated by Ivory
 * Compiler version  0.1.0.0
 */
#include "tower_task_usercode_control_121.h"

uint8_t armed_130;

struct flightmode flightmode_131;

struct userinput_result input_132;

struct controloutput control_133;

void eventhandler_control_121_chan0_178(const struct sensors_result* n_var0)
{
    read_control_121_dataport12_123(&armed_130);
    read_control_121_dataport10_125(&flightmode_131);
    read_control_121_dataport41_127(&input_132);
    
    uint8_t n_deref0 = *&armed_130;
    
    stabilize_run(n_deref0, &flightmode_131, &input_132, n_var0, &control_133);
    
    float n_deref1 = (&input_132)->throttle;
    
    *&(&control_133)->throttle = (n_deref1 + 1.0f) / 2.0f;
    emitFromTask_control_121_chan35_129(&control_133);
}

void stabilize_run(uint8_t n_var0, const struct flightmode* n_var1, const
                   struct userinput_result* n_var2, const
                   struct sensors_result* n_var3, struct controloutput* n_var4)
{
    if (n_var0 != 1U) {
        *&(&g_pid_roll_stabilize)->pid_reset = 1U;
        *&(&g_pid_pitch_stabilize)->pid_reset = 1U;
        *&(&g_pid_roll_rate)->pid_reset = 1U;
        *&(&g_pid_pitch_rate)->pid_reset = 1U;
        *&(&g_pid_yaw_rate)->pid_reset = 1U;
    } else {
        float n_deref0 = n_var3->roll;
        float n_deref1 = n_var3->pitch;
        float n_deref2 = n_var3->omega_x;
        float n_deref3 = n_var3->omega_y;
        float n_deref4 = n_var3->omega_z;
        float n_deref5 = n_var2->roll;
        float n_deref6 = n_var2->pitch;
        float n_deref7 = n_var2->yaw;
        struct PIDConfig n_local8 = {};
        struct PIDConfig* n_ref9 = &n_local8;
        float n_local10 = 0.0f;
        float* n_ref11 = &n_local10;
        
        read_control_121_dataport14_135(n_ref11);
        
        float n_deref12 = *n_ref11;
        
        *&n_ref9->pid_pGain = n_deref12;
        
        float n_local13 = 0.0f;
        float* n_ref14 = &n_local13;
        
        read_control_121_dataport15_137(n_ref14);
        
        float n_deref15 = *n_ref14;
        
        *&n_ref9->pid_iGain = n_deref15;
        
        float n_local16 = 0.0f;
        float* n_ref17 = &n_local16;
        
        read_control_121_dataport16_139(n_ref17);
        
        float n_deref18 = *n_ref17;
        
        *&n_ref9->pid_dGain = n_deref18;
        
        float n_local19 = 0.0f;
        float* n_ref20 = &n_local19;
        
        read_control_121_dataport17_141(n_ref20);
        
        float n_deref21 = *n_ref20;
        
        *&n_ref9->pid_iMax = n_deref21;
        
        float n_local22 = 0.0f;
        float* n_ref23 = &n_local22;
        
        read_control_121_dataport17_141(n_ref23);
        
        float n_deref24 = *n_ref23;
        
        *&n_ref9->pid_iMin = -n_deref24;
        
        struct PIDConfig n_local25 = {};
        struct PIDConfig* n_ref26 = &n_local25;
        float n_local27 = 0.0f;
        float* n_ref28 = &n_local27;
        
        read_control_121_dataport18_143(n_ref28);
        
        float n_deref29 = *n_ref28;
        
        *&n_ref26->pid_pGain = n_deref29;
        
        float n_local30 = 0.0f;
        float* n_ref31 = &n_local30;
        
        read_control_121_dataport19_145(n_ref31);
        
        float n_deref32 = *n_ref31;
        
        *&n_ref26->pid_iGain = n_deref32;
        
        float n_local33 = 0.0f;
        float* n_ref34 = &n_local33;
        
        read_control_121_dataport20_147(n_ref34);
        
        float n_deref35 = *n_ref34;
        
        *&n_ref26->pid_dGain = n_deref35;
        
        float n_local36 = 0.0f;
        float* n_ref37 = &n_local36;
        
        read_control_121_dataport21_149(n_ref37);
        
        float n_deref38 = *n_ref37;
        
        *&n_ref26->pid_iMax = n_deref38;
        
        float n_local39 = 0.0f;
        float* n_ref40 = &n_local39;
        
        read_control_121_dataport21_149(n_ref40);
        
        float n_deref41 = *n_ref40;
        
        *&n_ref26->pid_iMin = -n_deref41;
        
        struct PIDConfig n_local42 = {};
        struct PIDConfig* n_ref43 = &n_local42;
        float n_local44 = 0.0f;
        float* n_ref45 = &n_local44;
        
        read_control_121_dataport22_151(n_ref45);
        
        float n_deref46 = *n_ref45;
        
        *&n_ref43->pid_pGain = n_deref46;
        
        float n_local47 = 0.0f;
        float* n_ref48 = &n_local47;
        
        read_control_121_dataport23_153(n_ref48);
        
        float n_deref49 = *n_ref48;
        
        *&n_ref43->pid_iGain = n_deref49;
        
        float n_local50 = 0.0f;
        float* n_ref51 = &n_local50;
        
        read_control_121_dataport24_155(n_ref51);
        
        float n_deref52 = *n_ref51;
        
        *&n_ref43->pid_dGain = n_deref52;
        
        float n_local53 = 0.0f;
        float* n_ref54 = &n_local53;
        
        read_control_121_dataport25_157(n_ref54);
        
        float n_deref55 = *n_ref54;
        
        *&n_ref43->pid_iMax = n_deref55;
        
        float n_local56 = 0.0f;
        float* n_ref57 = &n_local56;
        
        read_control_121_dataport25_157(n_ref57);
        
        float n_deref58 = *n_ref57;
        
        *&n_ref43->pid_iMin = -n_deref58;
        
        struct PIDConfig n_local59 = {};
        struct PIDConfig* n_ref60 = &n_local59;
        float n_local61 = 0.0f;
        float* n_ref62 = &n_local61;
        
        read_control_121_dataport26_159(n_ref62);
        
        float n_deref63 = *n_ref62;
        
        *&n_ref60->pid_pGain = n_deref63;
        
        float n_local64 = 0.0f;
        float* n_ref65 = &n_local64;
        
        read_control_121_dataport27_161(n_ref65);
        
        float n_deref66 = *n_ref65;
        
        *&n_ref60->pid_iGain = n_deref66;
        
        float n_local67 = 0.0f;
        float* n_ref68 = &n_local67;
        
        read_control_121_dataport28_163(n_ref68);
        
        float n_deref69 = *n_ref68;
        
        *&n_ref60->pid_dGain = n_deref69;
        
        float n_local70 = 0.0f;
        float* n_ref71 = &n_local70;
        
        read_control_121_dataport29_165(n_ref71);
        
        float n_deref72 = *n_ref71;
        
        *&n_ref60->pid_iMax = n_deref72;
        
        float n_local73 = 0.0f;
        float* n_ref74 = &n_local73;
        
        read_control_121_dataport29_165(n_ref74);
        
        float n_deref75 = *n_ref74;
        
        *&n_ref60->pid_iMin = -n_deref75;
        
        struct PIDConfig n_local76 = {};
        struct PIDConfig* n_ref77 = &n_local76;
        float n_local78 = 0.0f;
        float* n_ref79 = &n_local78;
        
        read_control_121_dataport30_167(n_ref79);
        
        float n_deref80 = *n_ref79;
        
        *&n_ref77->pid_pGain = n_deref80;
        
        float n_local81 = 0.0f;
        float* n_ref82 = &n_local81;
        
        read_control_121_dataport31_169(n_ref82);
        
        float n_deref83 = *n_ref82;
        
        *&n_ref77->pid_iGain = n_deref83;
        
        float n_local84 = 0.0f;
        float* n_ref85 = &n_local84;
        
        read_control_121_dataport32_171(n_ref85);
        
        float n_deref86 = *n_ref85;
        
        *&n_ref77->pid_dGain = n_deref86;
        
        float n_local87 = 0.0f;
        float* n_ref88 = &n_local87;
        
        read_control_121_dataport33_173(n_ref88);
        
        float n_deref89 = *n_ref88;
        
        *&n_ref77->pid_iMax = n_deref89;
        
        float n_local90 = 0.0f;
        float* n_ref91 = &n_local90;
        
        read_control_121_dataport33_173(n_ref91);
        
        float n_deref92 = *n_ref91;
        
        *&n_ref77->pid_iMin = -n_deref92;
        
        float n_r93 = stabilize_from_angle(&g_pid_roll_stabilize, n_ref9,
                                           &g_pid_roll_rate, n_ref26, 45.0f,
                                           n_deref5, n_deref0, n_deref2, 50.0f);
        float n_r94 = stabilize_from_angle(&g_pid_pitch_stabilize, n_ref43,
                                           &g_pid_pitch_rate, n_ref60, 45.0f,
                                           -(1.0f * n_deref6), n_deref1,
                                           n_deref3, 50.0f);
        float n_r95 = stabilize_from_rate(&g_pid_yaw_rate, n_ref77, n_deref7,
                                          180.0f, n_deref4, 45.0f);
        
        *&n_var4->roll = n_r93;
        *&n_var4->pitch = n_r94;
        *&n_var4->yaw = n_r95;
    }
}