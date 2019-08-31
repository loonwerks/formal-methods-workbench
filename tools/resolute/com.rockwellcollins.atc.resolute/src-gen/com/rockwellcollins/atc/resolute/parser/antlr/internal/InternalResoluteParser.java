package com.rockwellcollins.atc.resolute.parser.antlr.internal; 

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.rockwellcollins.atc.resolute.services.ResoluteGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class InternalResoluteParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "Provides_subprogram_group_access", "Requires_subprogram_group_access", "Provides_subprogram_access", "Requires_subprogram_access", "Subprogram_group_access", "Error_state_reachable", "Is_virtual_processor", "Provides_data_access", "Requires_data_access", "Flow_specifications", "Is_abstract_feature", "Provides_bus_access", "Requires_bus_access", "Flow_specification", "Enumerated_values", "Subprogram_access", "Virtual_processor", "End_to_end_flows", "Flow_destination", "Is_bidirectional", "Subprogram_group", "End_to_end_flow", "Event_data_port", "Is_thread_group", "Propagate_error", "Property_member", "Has_prototypes", "Is_data_access", "Is_virtual_bus", "Contain_error", "Feature_group", "Flow_elements", "Is_bus_access", "Is_event_port", "Is_subprogram", "Receive_error", "Subcomponents", "Has_property", "Is_data_port", "Is_processor", "Thread_group", "Connections", "Data_access", "Destination", "Flow_source", "Is_bound_to", "Is_in_array", "Lower_bound", "Upper_bound", "Virtual_bus", "Assumption", "Bus_access", "Classifier", "Connection", "Event_port", "Has_member", "Has_parent", "Instanceof", "Is_of_type", "Is_process", "Subprogram", "Component", "Data_port", "Direction", "Has_modes", "Instances", "Intersect", "Is_device", "Is_memory", "Is_system", "Is_thread", "Processor", "Reference", "Abstract", "Analysis", "Constant", "Features", "Has_type", "Instance", "Property", "Andthen", "Applies", "As_list", "Binding", "Compute", "Context", "Feature", "Is_data", "Is_port", "Process", "Ruleset", "Warning", "Access", "Append", "As_set", "Device", "Exists", "Forall", "Is_bus", "Length", "Member", "Memory", "Orelse", "Parent", "Source", "String", "KW_System", "Thread", "Check", "Debug", "Delta", "Error", "False", "Modes", "Prove", "Range", "Union", "Aadl", "Bool", "Data", "Else", "Fail", "Head", "Info", "Name", "Port", "Real", "Size", "Tail", "Then", "This", "True", "Type", "PlusSignEqualsSignGreaterThanSign", "And", "Bus", "For", "Int", "Let", "Not", "Sum", "AsteriskAsterisk", "FullStopFullStop", "ColonColon", "LessThanSignEqualsSign", "LessThanSignGreaterThanSign", "EqualsSignGreaterThanSign", "GreaterThanSignEqualsSign", "If", "In", "Or", "To", "PercentSign", "LeftParenthesis", "RightParenthesis", "Asterisk", "PlusSign", "Comma", "HyphenMinus", "FullStop", "Solidus", "Colon", "Semicolon", "LessThanSign", "EqualsSign", "GreaterThanSign", "LeftSquareBracket", "RightSquareBracket", "CircumflexAccent", "LeftCurlyBracket", "VerticalLine", "RightCurlyBracket", "RULE_SL_COMMENT", "RULE_DIGIT", "RULE_EXPONENT", "RULE_INT_EXPONENT", "RULE_REAL_LIT", "RULE_BASED_INTEGER", "RULE_INTEGER_LIT", "RULE_EXTENDED_DIGIT", "RULE_STRING", "RULE_ID", "RULE_WS"
    };
    public static final int Connections=45;
    public static final int LessThanSignGreaterThanSign=149;
    public static final int Requires_subprogram_group_access=5;
    public static final int Or=154;
    public static final int Is_port=92;
    public static final int EqualsSignGreaterThanSign=150;
    public static final int Data_access=46;
    public static final int Memory=105;
    public static final int KW_System=110;
    public static final int Size=131;
    public static final int Access=96;
    public static final int Check=112;
    public static final int Provides_data_access=11;
    public static final int String=109;
    public static final int False=116;
    public static final int Feature=90;
    public static final int LessThanSign=167;
    public static final int Thread_group=44;
    public static final int Has_member=59;
    public static final int Data_port=66;
    public static final int PlusSignEqualsSignGreaterThanSign=137;
    public static final int LeftParenthesis=157;
    public static final int Bool=122;
    public static final int Is_bus=102;
    public static final int Then=133;
    public static final int Requires_bus_access=16;
    public static final int Fail=125;
    public static final int GreaterThanSign=169;
    public static final int Head=126;
    public static final int RULE_ID=185;
    public static final int Is_virtual_bus=32;
    public static final int RULE_DIGIT=177;
    public static final int Sum=144;
    public static final int GreaterThanSignEqualsSign=151;
    public static final int Flow_elements=35;
    public static final int ColonColon=147;
    public static final int Has_prototypes=30;
    public static final int Is_data=91;
    public static final int Is_bus_access=36;
    public static final int Has_property=41;
    public static final int VerticalLine=174;
    public static final int Is_bidirectional=23;
    public static final int Analysis=78;
    public static final int PlusSign=160;
    public static final int Is_data_access=31;
    public static final int LeftSquareBracket=170;
    public static final int If=152;
    public static final int Lower_bound=51;
    public static final int Is_of_type=62;
    public static final int Provides_subprogram_access=6;
    public static final int Ruleset=94;
    public static final int Warning=95;
    public static final int Processor=75;
    public static final int Instance=82;
    public static final int In=153;
    public static final int RULE_REAL_LIT=180;
    public static final int Contain_error=33;
    public static final int Property_member=29;
    public static final int Classifier=56;
    public static final int Is_memory=72;
    public static final int Direction=67;
    public static final int Union=120;
    public static final int Assumption=54;
    public static final int Comma=161;
    public static final int HyphenMinus=162;
    public static final int Requires_data_access=12;
    public static final int Is_subprogram=38;
    public static final int Tail=132;
    public static final int Event_port=58;
    public static final int LessThanSignEqualsSign=148;
    public static final int Solidus=164;
    public static final int RightCurlyBracket=175;
    public static final int Property=83;
    public static final int Context=89;
    public static final int Prove=118;
    public static final int Modes=117;
    public static final int Bus=139;
    public static final int FullStop=163;
    public static final int Is_thread=74;
    public static final int Has_modes=68;
    public static final int Reference=76;
    public static final int Abstract=77;
    public static final int Connection=57;
    public static final int Provides_bus_access=15;
    public static final int Is_device=71;
    public static final int Thread=111;
    public static final int Instances=69;
    public static final int Semicolon=166;
    public static final int Type=136;
    public static final int RULE_EXPONENT=178;
    public static final int Append=97;
    public static final int As_list=86;
    public static final int Length=103;
    public static final int Delta=114;
    public static final int Else=124;
    public static final int RULE_EXTENDED_DIGIT=183;
    public static final int Is_virtual_processor=10;
    public static final int Virtual_bus=53;
    public static final int Flow_specifications=13;
    public static final int End_to_end_flows=21;
    public static final int Receive_error=39;
    public static final int Port=129;
    public static final int True=135;
    public static final int Subprogram_group=24;
    public static final int Process=93;
    public static final int Error_state_reachable=9;
    public static final int Requires_subprogram_access=7;
    public static final int Name=128;
    public static final int RULE_INT_EXPONENT=179;
    public static final int PercentSign=156;
    public static final int Enumerated_values=18;
    public static final int Intersect=70;
    public static final int Upper_bound=52;
    public static final int FullStopFullStop=146;
    public static final int Real=130;
    public static final int As_set=98;
    public static final int This=134;
    public static final int To=155;
    public static final int Applies=85;
    public static final int Forall=101;
    public static final int Member=104;
    public static final int RULE_BASED_INTEGER=181;
    public static final int RightSquareBracket=171;
    public static final int Is_in_array=50;
    public static final int Binding=87;
    public static final int Flow_destination=22;
    public static final int Subprogram_group_access=8;
    public static final int Device=99;
    public static final int For=140;
    public static final int RightParenthesis=158;
    public static final int Is_event_port=37;
    public static final int Range=119;
    public static final int Is_process=63;
    public static final int Not=143;
    public static final int Is_bound_to=49;
    public static final int Andthen=84;
    public static final int And=138;
    public static final int AsteriskAsterisk=145;
    public static final int Is_processor=43;
    public static final int Subcomponents=40;
    public static final int Is_data_port=42;
    public static final int Subprogram=64;
    public static final int RULE_INTEGER_LIT=182;
    public static final int Destination=47;
    public static final int Parent=107;
    public static final int Constant=79;
    public static final int RULE_STRING=184;
    public static final int Source=108;
    public static final int Int=141;
    public static final int RULE_SL_COMMENT=176;
    public static final int Flow_specification=17;
    public static final int EqualsSign=168;
    public static final int Provides_subprogram_group_access=4;
    public static final int Bus_access=55;
    public static final int Has_parent=60;
    public static final int Aadl=121;
    public static final int Instanceof=61;
    public static final int Features=80;
    public static final int Colon=165;
    public static final int Component=65;
    public static final int EOF=-1;
    public static final int Subprogram_access=19;
    public static final int Asterisk=159;
    public static final int Debug=113;
    public static final int Has_type=81;
    public static final int Is_abstract_feature=14;
    public static final int RULE_WS=186;
    public static final int LeftCurlyBracket=173;
    public static final int Error=115;
    public static final int Data=123;
    public static final int Propagate_error=28;
    public static final int Info=127;
    public static final int Event_data_port=26;
    public static final int End_to_end_flow=25;
    public static final int Feature_group=34;
    public static final int CircumflexAccent=172;
    public static final int Flow_source=48;
    public static final int Is_thread_group=27;
    public static final int Is_system=73;
    public static final int Exists=100;
    public static final int Compute=88;
    public static final int Virtual_processor=20;
    public static final int Orelse=106;
    public static final int Let=142;

    // delegates
    // delegators


        public InternalResoluteParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalResoluteParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalResoluteParser.tokenNames; }
    public String getGrammarFileName() { return "InternalResoluteParser.g"; }




    	private ResoluteGrammarAccess grammarAccess;
    	 	
    	public InternalResoluteParser(TokenStream input, ResoluteGrammarAccess grammarAccess) {
    		this(input);
    		this.grammarAccess = grammarAccess;
    		registerRules(grammarAccess.getGrammar());
    	}
    	
    	@Override
    	protected String getFirstRuleName() {
    		return "AnnexLibrary";	
    	} 
    	   	   	
    	@Override
    	protected ResoluteGrammarAccess getGrammarAccess() {
    		return grammarAccess;
    	}



    // $ANTLR start "entryRuleAnnexLibrary"
    // InternalResoluteParser.g:61:1: entryRuleAnnexLibrary returns [EObject current=null] : iv_ruleAnnexLibrary= ruleAnnexLibrary EOF ;
    public final EObject entryRuleAnnexLibrary() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAnnexLibrary = null;


        try {
            // InternalResoluteParser.g:62:2: (iv_ruleAnnexLibrary= ruleAnnexLibrary EOF )
            // InternalResoluteParser.g:63:2: iv_ruleAnnexLibrary= ruleAnnexLibrary EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAnnexLibraryRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAnnexLibrary=ruleAnnexLibrary();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAnnexLibrary; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAnnexLibrary"


    // $ANTLR start "ruleAnnexLibrary"
    // InternalResoluteParser.g:70:1: ruleAnnexLibrary returns [EObject current=null] : this_ResoluteLibrary_0= ruleResoluteLibrary ;
    public final EObject ruleAnnexLibrary() throws RecognitionException {
        EObject current = null;

        EObject this_ResoluteLibrary_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:73:28: (this_ResoluteLibrary_0= ruleResoluteLibrary )
            // InternalResoluteParser.g:75:5: this_ResoluteLibrary_0= ruleResoluteLibrary
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAnnexLibraryAccess().getResoluteLibraryParserRuleCall()); 
                  
            }
            pushFollow(FollowSets000.FOLLOW_2);
            this_ResoluteLibrary_0=ruleResoluteLibrary();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current = this_ResoluteLibrary_0;
                      afterParserOrEnumRuleCall();
                  
            }

            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAnnexLibrary"


    // $ANTLR start "entryRuleResoluteLibrary"
    // InternalResoluteParser.g:99:1: entryRuleResoluteLibrary returns [EObject current=null] : iv_ruleResoluteLibrary= ruleResoluteLibrary EOF ;
    public final EObject entryRuleResoluteLibrary() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleResoluteLibrary = null;


        try {
            // InternalResoluteParser.g:100:2: (iv_ruleResoluteLibrary= ruleResoluteLibrary EOF )
            // InternalResoluteParser.g:101:2: iv_ruleResoluteLibrary= ruleResoluteLibrary EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getResoluteLibraryRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleResoluteLibrary=ruleResoluteLibrary();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleResoluteLibrary; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleResoluteLibrary"


    // $ANTLR start "ruleResoluteLibrary"
    // InternalResoluteParser.g:108:1: ruleResoluteLibrary returns [EObject current=null] : ( () ( (lv_definitions_1_0= ruleDefinition ) )* ) ;
    public final EObject ruleResoluteLibrary() throws RecognitionException {
        EObject current = null;

        EObject lv_definitions_1_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:111:28: ( ( () ( (lv_definitions_1_0= ruleDefinition ) )* ) )
            // InternalResoluteParser.g:112:1: ( () ( (lv_definitions_1_0= ruleDefinition ) )* )
            {
            // InternalResoluteParser.g:112:1: ( () ( (lv_definitions_1_0= ruleDefinition ) )* )
            // InternalResoluteParser.g:112:2: () ( (lv_definitions_1_0= ruleDefinition ) )*
            {
            // InternalResoluteParser.g:112:2: ()
            // InternalResoluteParser.g:113:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getResoluteLibraryAccess().getResoluteLibraryAction_0(),
                          current);
                  
            }

            }

            // InternalResoluteParser.g:118:2: ( (lv_definitions_1_0= ruleDefinition ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==Ruleset||LA1_0==RULE_ID) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalResoluteParser.g:119:1: (lv_definitions_1_0= ruleDefinition )
            	    {
            	    // InternalResoluteParser.g:119:1: (lv_definitions_1_0= ruleDefinition )
            	    // InternalResoluteParser.g:120:3: lv_definitions_1_0= ruleDefinition
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getResoluteLibraryAccess().getDefinitionsDefinitionParserRuleCall_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_3);
            	    lv_definitions_1_0=ruleDefinition();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getResoluteLibraryRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"definitions",
            	              		lv_definitions_1_0, 
            	              		"com.rockwellcollins.atc.resolute.Resolute.Definition");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleResoluteLibrary"


    // $ANTLR start "entryRuleDefinition"
    // InternalResoluteParser.g:144:1: entryRuleDefinition returns [EObject current=null] : iv_ruleDefinition= ruleDefinition EOF ;
    public final EObject entryRuleDefinition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefinition = null;


        try {
            // InternalResoluteParser.g:145:2: (iv_ruleDefinition= ruleDefinition EOF )
            // InternalResoluteParser.g:146:2: iv_ruleDefinition= ruleDefinition EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefinitionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDefinition=ruleDefinition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefinition; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDefinition"


    // $ANTLR start "ruleDefinition"
    // InternalResoluteParser.g:153:1: ruleDefinition returns [EObject current=null] : (this_ConstantDefinition_0= ruleConstantDefinition | this_FunctionDefinition_1= ruleFunctionDefinition | this_Ruleset_2= ruleRuleset ) ;
    public final EObject ruleDefinition() throws RecognitionException {
        EObject current = null;

        EObject this_ConstantDefinition_0 = null;

        EObject this_FunctionDefinition_1 = null;

        EObject this_Ruleset_2 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:156:28: ( (this_ConstantDefinition_0= ruleConstantDefinition | this_FunctionDefinition_1= ruleFunctionDefinition | this_Ruleset_2= ruleRuleset ) )
            // InternalResoluteParser.g:157:1: (this_ConstantDefinition_0= ruleConstantDefinition | this_FunctionDefinition_1= ruleFunctionDefinition | this_Ruleset_2= ruleRuleset )
            {
            // InternalResoluteParser.g:157:1: (this_ConstantDefinition_0= ruleConstantDefinition | this_FunctionDefinition_1= ruleFunctionDefinition | this_Ruleset_2= ruleRuleset )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==RULE_ID) ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1==Colon) ) {
                    alt2=1;
                }
                else if ( (LA2_1==LeftParenthesis) ) {
                    alt2=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA2_0==Ruleset) ) {
                alt2=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // InternalResoluteParser.g:158:5: this_ConstantDefinition_0= ruleConstantDefinition
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getDefinitionAccess().getConstantDefinitionParserRuleCall_0()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ConstantDefinition_0=ruleConstantDefinition();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_ConstantDefinition_0;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:168:5: this_FunctionDefinition_1= ruleFunctionDefinition
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getDefinitionAccess().getFunctionDefinitionParserRuleCall_1()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_FunctionDefinition_1=ruleFunctionDefinition();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_FunctionDefinition_1;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // InternalResoluteParser.g:178:5: this_Ruleset_2= ruleRuleset
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getDefinitionAccess().getRulesetParserRuleCall_2()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_Ruleset_2=ruleRuleset();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_Ruleset_2;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDefinition"


    // $ANTLR start "entryRuleType"
    // InternalResoluteParser.g:194:1: entryRuleType returns [EObject current=null] : iv_ruleType= ruleType EOF ;
    public final EObject entryRuleType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleType = null;


        try {
            // InternalResoluteParser.g:195:2: (iv_ruleType= ruleType EOF )
            // InternalResoluteParser.g:196:2: iv_ruleType= ruleType EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTypeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleType=ruleType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleType; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleType"


    // $ANTLR start "ruleType"
    // InternalResoluteParser.g:203:1: ruleType returns [EObject current=null] : ( ( () otherlv_1= LeftSquareBracket ( (lv_type_2_0= ruleType ) ) otherlv_3= RightSquareBracket ) | ( () otherlv_5= LeftCurlyBracket ( (lv_type_6_0= ruleType ) ) otherlv_7= RightCurlyBracket ) | (this_BaseType_8= ruleBaseType (otherlv_9= LessThanSign ( (lv_paramType_10_0= ruleType ) ) otherlv_11= GreaterThanSign )? ) | ( () ( (lv_libName_13_0= RULE_ID ) ) otherlv_14= FullStop ( (lv_fnType_15_0= RULE_ID ) ) ) ) ;
    public final EObject ruleType() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token lv_libName_13_0=null;
        Token otherlv_14=null;
        Token lv_fnType_15_0=null;
        EObject lv_type_2_0 = null;

        EObject lv_type_6_0 = null;

        EObject this_BaseType_8 = null;

        EObject lv_paramType_10_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:206:28: ( ( ( () otherlv_1= LeftSquareBracket ( (lv_type_2_0= ruleType ) ) otherlv_3= RightSquareBracket ) | ( () otherlv_5= LeftCurlyBracket ( (lv_type_6_0= ruleType ) ) otherlv_7= RightCurlyBracket ) | (this_BaseType_8= ruleBaseType (otherlv_9= LessThanSign ( (lv_paramType_10_0= ruleType ) ) otherlv_11= GreaterThanSign )? ) | ( () ( (lv_libName_13_0= RULE_ID ) ) otherlv_14= FullStop ( (lv_fnType_15_0= RULE_ID ) ) ) ) )
            // InternalResoluteParser.g:207:1: ( ( () otherlv_1= LeftSquareBracket ( (lv_type_2_0= ruleType ) ) otherlv_3= RightSquareBracket ) | ( () otherlv_5= LeftCurlyBracket ( (lv_type_6_0= ruleType ) ) otherlv_7= RightCurlyBracket ) | (this_BaseType_8= ruleBaseType (otherlv_9= LessThanSign ( (lv_paramType_10_0= ruleType ) ) otherlv_11= GreaterThanSign )? ) | ( () ( (lv_libName_13_0= RULE_ID ) ) otherlv_14= FullStop ( (lv_fnType_15_0= RULE_ID ) ) ) )
            {
            // InternalResoluteParser.g:207:1: ( ( () otherlv_1= LeftSquareBracket ( (lv_type_2_0= ruleType ) ) otherlv_3= RightSquareBracket ) | ( () otherlv_5= LeftCurlyBracket ( (lv_type_6_0= ruleType ) ) otherlv_7= RightCurlyBracket ) | (this_BaseType_8= ruleBaseType (otherlv_9= LessThanSign ( (lv_paramType_10_0= ruleType ) ) otherlv_11= GreaterThanSign )? ) | ( () ( (lv_libName_13_0= RULE_ID ) ) otherlv_14= FullStop ( (lv_fnType_15_0= RULE_ID ) ) ) )
            int alt4=4;
            switch ( input.LA(1) ) {
            case LeftSquareBracket:
                {
                alt4=1;
                }
                break;
            case LeftCurlyBracket:
                {
                alt4=2;
                }
                break;
            case Provides_subprogram_group_access:
            case Requires_subprogram_group_access:
            case Provides_subprogram_access:
            case Requires_subprogram_access:
            case Subprogram_group_access:
            case Provides_data_access:
            case Requires_data_access:
            case Provides_bus_access:
            case Requires_bus_access:
            case Flow_specification:
            case Subprogram_access:
            case Virtual_processor:
            case Subprogram_group:
            case End_to_end_flow:
            case Event_data_port:
            case Feature_group:
            case Thread_group:
            case Data_access:
            case Virtual_bus:
            case Bus_access:
            case Connection:
            case Event_port:
            case Subprogram:
            case Component:
            case Data_port:
            case Processor:
            case Abstract:
            case Property:
            case Feature:
            case Process:
            case Access:
            case Device:
            case Memory:
            case String:
            case KW_System:
            case Thread:
            case Range:
            case Aadl:
            case Bool:
            case Data:
            case Port:
            case Real:
            case Bus:
            case Int:
                {
                alt4=3;
                }
                break;
            case RULE_ID:
                {
                alt4=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // InternalResoluteParser.g:207:2: ( () otherlv_1= LeftSquareBracket ( (lv_type_2_0= ruleType ) ) otherlv_3= RightSquareBracket )
                    {
                    // InternalResoluteParser.g:207:2: ( () otherlv_1= LeftSquareBracket ( (lv_type_2_0= ruleType ) ) otherlv_3= RightSquareBracket )
                    // InternalResoluteParser.g:207:3: () otherlv_1= LeftSquareBracket ( (lv_type_2_0= ruleType ) ) otherlv_3= RightSquareBracket
                    {
                    // InternalResoluteParser.g:207:3: ()
                    // InternalResoluteParser.g:208:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTypeAccess().getListTypeAction_0_0(),
                                  current);
                          
                    }

                    }

                    otherlv_1=(Token)match(input,LeftSquareBracket,FollowSets000.FOLLOW_4); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getTypeAccess().getLeftSquareBracketKeyword_0_1());
                          
                    }
                    // InternalResoluteParser.g:218:1: ( (lv_type_2_0= ruleType ) )
                    // InternalResoluteParser.g:219:1: (lv_type_2_0= ruleType )
                    {
                    // InternalResoluteParser.g:219:1: (lv_type_2_0= ruleType )
                    // InternalResoluteParser.g:220:3: lv_type_2_0= ruleType
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getTypeAccess().getTypeTypeParserRuleCall_0_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_5);
                    lv_type_2_0=ruleType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getTypeRule());
                      	        }
                             		set(
                             			current, 
                             			"type",
                              		lv_type_2_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Type");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_3=(Token)match(input,RightSquareBracket,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_3, grammarAccess.getTypeAccess().getRightSquareBracketKeyword_0_3());
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:242:6: ( () otherlv_5= LeftCurlyBracket ( (lv_type_6_0= ruleType ) ) otherlv_7= RightCurlyBracket )
                    {
                    // InternalResoluteParser.g:242:6: ( () otherlv_5= LeftCurlyBracket ( (lv_type_6_0= ruleType ) ) otherlv_7= RightCurlyBracket )
                    // InternalResoluteParser.g:242:7: () otherlv_5= LeftCurlyBracket ( (lv_type_6_0= ruleType ) ) otherlv_7= RightCurlyBracket
                    {
                    // InternalResoluteParser.g:242:7: ()
                    // InternalResoluteParser.g:243:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTypeAccess().getSetTypeAction_1_0(),
                                  current);
                          
                    }

                    }

                    otherlv_5=(Token)match(input,LeftCurlyBracket,FollowSets000.FOLLOW_4); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_5, grammarAccess.getTypeAccess().getLeftCurlyBracketKeyword_1_1());
                          
                    }
                    // InternalResoluteParser.g:253:1: ( (lv_type_6_0= ruleType ) )
                    // InternalResoluteParser.g:254:1: (lv_type_6_0= ruleType )
                    {
                    // InternalResoluteParser.g:254:1: (lv_type_6_0= ruleType )
                    // InternalResoluteParser.g:255:3: lv_type_6_0= ruleType
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getTypeAccess().getTypeTypeParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_6);
                    lv_type_6_0=ruleType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getTypeRule());
                      	        }
                             		set(
                             			current, 
                             			"type",
                              		lv_type_6_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Type");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_7=(Token)match(input,RightCurlyBracket,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_7, grammarAccess.getTypeAccess().getRightCurlyBracketKeyword_1_3());
                          
                    }

                    }


                    }
                    break;
                case 3 :
                    // InternalResoluteParser.g:277:6: (this_BaseType_8= ruleBaseType (otherlv_9= LessThanSign ( (lv_paramType_10_0= ruleType ) ) otherlv_11= GreaterThanSign )? )
                    {
                    // InternalResoluteParser.g:277:6: (this_BaseType_8= ruleBaseType (otherlv_9= LessThanSign ( (lv_paramType_10_0= ruleType ) ) otherlv_11= GreaterThanSign )? )
                    // InternalResoluteParser.g:278:5: this_BaseType_8= ruleBaseType (otherlv_9= LessThanSign ( (lv_paramType_10_0= ruleType ) ) otherlv_11= GreaterThanSign )?
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getTypeAccess().getBaseTypeParserRuleCall_2_0()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_7);
                    this_BaseType_8=ruleBaseType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_BaseType_8;
                              afterParserOrEnumRuleCall();
                          
                    }
                    // InternalResoluteParser.g:286:1: (otherlv_9= LessThanSign ( (lv_paramType_10_0= ruleType ) ) otherlv_11= GreaterThanSign )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==LessThanSign) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // InternalResoluteParser.g:287:2: otherlv_9= LessThanSign ( (lv_paramType_10_0= ruleType ) ) otherlv_11= GreaterThanSign
                            {
                            otherlv_9=(Token)match(input,LessThanSign,FollowSets000.FOLLOW_4); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                  	newLeafNode(otherlv_9, grammarAccess.getTypeAccess().getLessThanSignKeyword_2_1_0());
                                  
                            }
                            // InternalResoluteParser.g:291:1: ( (lv_paramType_10_0= ruleType ) )
                            // InternalResoluteParser.g:292:1: (lv_paramType_10_0= ruleType )
                            {
                            // InternalResoluteParser.g:292:1: (lv_paramType_10_0= ruleType )
                            // InternalResoluteParser.g:293:3: lv_paramType_10_0= ruleType
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getTypeAccess().getParamTypeTypeParserRuleCall_2_1_1_0()); 
                              	    
                            }
                            pushFollow(FollowSets000.FOLLOW_8);
                            lv_paramType_10_0=ruleType();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getTypeRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"paramType",
                                      		lv_paramType_10_0, 
                                      		"com.rockwellcollins.atc.resolute.Resolute.Type");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }

                            otherlv_11=(Token)match(input,GreaterThanSign,FollowSets000.FOLLOW_2); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                  	newLeafNode(otherlv_11, grammarAccess.getTypeAccess().getGreaterThanSignKeyword_2_1_2());
                                  
                            }

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalResoluteParser.g:315:6: ( () ( (lv_libName_13_0= RULE_ID ) ) otherlv_14= FullStop ( (lv_fnType_15_0= RULE_ID ) ) )
                    {
                    // InternalResoluteParser.g:315:6: ( () ( (lv_libName_13_0= RULE_ID ) ) otherlv_14= FullStop ( (lv_fnType_15_0= RULE_ID ) ) )
                    // InternalResoluteParser.g:315:7: () ( (lv_libName_13_0= RULE_ID ) ) otherlv_14= FullStop ( (lv_fnType_15_0= RULE_ID ) )
                    {
                    // InternalResoluteParser.g:315:7: ()
                    // InternalResoluteParser.g:316:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTypeAccess().getLibraryFnTypeAction_3_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:321:2: ( (lv_libName_13_0= RULE_ID ) )
                    // InternalResoluteParser.g:322:1: (lv_libName_13_0= RULE_ID )
                    {
                    // InternalResoluteParser.g:322:1: (lv_libName_13_0= RULE_ID )
                    // InternalResoluteParser.g:323:3: lv_libName_13_0= RULE_ID
                    {
                    lv_libName_13_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_9); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_libName_13_0, grammarAccess.getTypeAccess().getLibNameIDTerminalRuleCall_3_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getTypeRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"libName",
                              		lv_libName_13_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.ID");
                      	    
                    }

                    }


                    }

                    otherlv_14=(Token)match(input,FullStop,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_14, grammarAccess.getTypeAccess().getFullStopKeyword_3_2());
                          
                    }
                    // InternalResoluteParser.g:344:1: ( (lv_fnType_15_0= RULE_ID ) )
                    // InternalResoluteParser.g:345:1: (lv_fnType_15_0= RULE_ID )
                    {
                    // InternalResoluteParser.g:345:1: (lv_fnType_15_0= RULE_ID )
                    // InternalResoluteParser.g:346:3: lv_fnType_15_0= RULE_ID
                    {
                    lv_fnType_15_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_fnType_15_0, grammarAccess.getTypeAccess().getFnTypeIDTerminalRuleCall_3_3_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getTypeRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"fnType",
                              		lv_fnType_15_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.ID");
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleType"


    // $ANTLR start "entryRuleBaseType"
    // InternalResoluteParser.g:370:1: entryRuleBaseType returns [EObject current=null] : iv_ruleBaseType= ruleBaseType EOF ;
    public final EObject entryRuleBaseType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBaseType = null;


        try {
            // InternalResoluteParser.g:371:2: (iv_ruleBaseType= ruleBaseType EOF )
            // InternalResoluteParser.g:372:2: iv_ruleBaseType= ruleBaseType EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBaseTypeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleBaseType=ruleBaseType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBaseType; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBaseType"


    // $ANTLR start "ruleBaseType"
    // InternalResoluteParser.g:379:1: ruleBaseType returns [EObject current=null] : ( ( (lv_type_0_1= Int | lv_type_0_2= Real | lv_type_0_3= String | lv_type_0_4= Bool | lv_type_0_5= Range | lv_type_0_6= Aadl | lv_type_0_7= Component | lv_type_0_8= Abstract | lv_type_0_9= Bus | lv_type_0_10= Data | lv_type_0_11= Device | lv_type_0_12= Memory | lv_type_0_13= Processor | lv_type_0_14= Process | lv_type_0_15= Subprogram_group | lv_type_0_16= Subprogram | lv_type_0_17= KW_System | lv_type_0_18= Thread_group | lv_type_0_19= Thread | lv_type_0_20= Virtual_bus | lv_type_0_21= Virtual_processor | lv_type_0_22= Connection | lv_type_0_23= Property | lv_type_0_24= Feature | lv_type_0_25= Port | lv_type_0_26= Data_port | lv_type_0_27= Event_port | lv_type_0_28= Event_data_port | lv_type_0_29= Feature_group | lv_type_0_30= Access | lv_type_0_31= Bus_access | lv_type_0_32= Provides_bus_access | lv_type_0_33= Requires_bus_access | lv_type_0_34= Data_access | lv_type_0_35= Provides_data_access | lv_type_0_36= Requires_data_access | lv_type_0_37= Subprogram_access | lv_type_0_38= Provides_subprogram_access | lv_type_0_39= Requires_subprogram_access | lv_type_0_40= Subprogram_group_access | lv_type_0_41= Provides_subprogram_group_access | lv_type_0_42= Requires_subprogram_group_access | lv_type_0_43= Flow_specification | lv_type_0_44= End_to_end_flow ) ) ) ;
    public final EObject ruleBaseType() throws RecognitionException {
        EObject current = null;

        Token lv_type_0_1=null;
        Token lv_type_0_2=null;
        Token lv_type_0_3=null;
        Token lv_type_0_4=null;
        Token lv_type_0_5=null;
        Token lv_type_0_6=null;
        Token lv_type_0_7=null;
        Token lv_type_0_8=null;
        Token lv_type_0_9=null;
        Token lv_type_0_10=null;
        Token lv_type_0_11=null;
        Token lv_type_0_12=null;
        Token lv_type_0_13=null;
        Token lv_type_0_14=null;
        Token lv_type_0_15=null;
        Token lv_type_0_16=null;
        Token lv_type_0_17=null;
        Token lv_type_0_18=null;
        Token lv_type_0_19=null;
        Token lv_type_0_20=null;
        Token lv_type_0_21=null;
        Token lv_type_0_22=null;
        Token lv_type_0_23=null;
        Token lv_type_0_24=null;
        Token lv_type_0_25=null;
        Token lv_type_0_26=null;
        Token lv_type_0_27=null;
        Token lv_type_0_28=null;
        Token lv_type_0_29=null;
        Token lv_type_0_30=null;
        Token lv_type_0_31=null;
        Token lv_type_0_32=null;
        Token lv_type_0_33=null;
        Token lv_type_0_34=null;
        Token lv_type_0_35=null;
        Token lv_type_0_36=null;
        Token lv_type_0_37=null;
        Token lv_type_0_38=null;
        Token lv_type_0_39=null;
        Token lv_type_0_40=null;
        Token lv_type_0_41=null;
        Token lv_type_0_42=null;
        Token lv_type_0_43=null;
        Token lv_type_0_44=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:382:28: ( ( ( (lv_type_0_1= Int | lv_type_0_2= Real | lv_type_0_3= String | lv_type_0_4= Bool | lv_type_0_5= Range | lv_type_0_6= Aadl | lv_type_0_7= Component | lv_type_0_8= Abstract | lv_type_0_9= Bus | lv_type_0_10= Data | lv_type_0_11= Device | lv_type_0_12= Memory | lv_type_0_13= Processor | lv_type_0_14= Process | lv_type_0_15= Subprogram_group | lv_type_0_16= Subprogram | lv_type_0_17= KW_System | lv_type_0_18= Thread_group | lv_type_0_19= Thread | lv_type_0_20= Virtual_bus | lv_type_0_21= Virtual_processor | lv_type_0_22= Connection | lv_type_0_23= Property | lv_type_0_24= Feature | lv_type_0_25= Port | lv_type_0_26= Data_port | lv_type_0_27= Event_port | lv_type_0_28= Event_data_port | lv_type_0_29= Feature_group | lv_type_0_30= Access | lv_type_0_31= Bus_access | lv_type_0_32= Provides_bus_access | lv_type_0_33= Requires_bus_access | lv_type_0_34= Data_access | lv_type_0_35= Provides_data_access | lv_type_0_36= Requires_data_access | lv_type_0_37= Subprogram_access | lv_type_0_38= Provides_subprogram_access | lv_type_0_39= Requires_subprogram_access | lv_type_0_40= Subprogram_group_access | lv_type_0_41= Provides_subprogram_group_access | lv_type_0_42= Requires_subprogram_group_access | lv_type_0_43= Flow_specification | lv_type_0_44= End_to_end_flow ) ) ) )
            // InternalResoluteParser.g:383:1: ( ( (lv_type_0_1= Int | lv_type_0_2= Real | lv_type_0_3= String | lv_type_0_4= Bool | lv_type_0_5= Range | lv_type_0_6= Aadl | lv_type_0_7= Component | lv_type_0_8= Abstract | lv_type_0_9= Bus | lv_type_0_10= Data | lv_type_0_11= Device | lv_type_0_12= Memory | lv_type_0_13= Processor | lv_type_0_14= Process | lv_type_0_15= Subprogram_group | lv_type_0_16= Subprogram | lv_type_0_17= KW_System | lv_type_0_18= Thread_group | lv_type_0_19= Thread | lv_type_0_20= Virtual_bus | lv_type_0_21= Virtual_processor | lv_type_0_22= Connection | lv_type_0_23= Property | lv_type_0_24= Feature | lv_type_0_25= Port | lv_type_0_26= Data_port | lv_type_0_27= Event_port | lv_type_0_28= Event_data_port | lv_type_0_29= Feature_group | lv_type_0_30= Access | lv_type_0_31= Bus_access | lv_type_0_32= Provides_bus_access | lv_type_0_33= Requires_bus_access | lv_type_0_34= Data_access | lv_type_0_35= Provides_data_access | lv_type_0_36= Requires_data_access | lv_type_0_37= Subprogram_access | lv_type_0_38= Provides_subprogram_access | lv_type_0_39= Requires_subprogram_access | lv_type_0_40= Subprogram_group_access | lv_type_0_41= Provides_subprogram_group_access | lv_type_0_42= Requires_subprogram_group_access | lv_type_0_43= Flow_specification | lv_type_0_44= End_to_end_flow ) ) )
            {
            // InternalResoluteParser.g:383:1: ( ( (lv_type_0_1= Int | lv_type_0_2= Real | lv_type_0_3= String | lv_type_0_4= Bool | lv_type_0_5= Range | lv_type_0_6= Aadl | lv_type_0_7= Component | lv_type_0_8= Abstract | lv_type_0_9= Bus | lv_type_0_10= Data | lv_type_0_11= Device | lv_type_0_12= Memory | lv_type_0_13= Processor | lv_type_0_14= Process | lv_type_0_15= Subprogram_group | lv_type_0_16= Subprogram | lv_type_0_17= KW_System | lv_type_0_18= Thread_group | lv_type_0_19= Thread | lv_type_0_20= Virtual_bus | lv_type_0_21= Virtual_processor | lv_type_0_22= Connection | lv_type_0_23= Property | lv_type_0_24= Feature | lv_type_0_25= Port | lv_type_0_26= Data_port | lv_type_0_27= Event_port | lv_type_0_28= Event_data_port | lv_type_0_29= Feature_group | lv_type_0_30= Access | lv_type_0_31= Bus_access | lv_type_0_32= Provides_bus_access | lv_type_0_33= Requires_bus_access | lv_type_0_34= Data_access | lv_type_0_35= Provides_data_access | lv_type_0_36= Requires_data_access | lv_type_0_37= Subprogram_access | lv_type_0_38= Provides_subprogram_access | lv_type_0_39= Requires_subprogram_access | lv_type_0_40= Subprogram_group_access | lv_type_0_41= Provides_subprogram_group_access | lv_type_0_42= Requires_subprogram_group_access | lv_type_0_43= Flow_specification | lv_type_0_44= End_to_end_flow ) ) )
            // InternalResoluteParser.g:384:1: ( (lv_type_0_1= Int | lv_type_0_2= Real | lv_type_0_3= String | lv_type_0_4= Bool | lv_type_0_5= Range | lv_type_0_6= Aadl | lv_type_0_7= Component | lv_type_0_8= Abstract | lv_type_0_9= Bus | lv_type_0_10= Data | lv_type_0_11= Device | lv_type_0_12= Memory | lv_type_0_13= Processor | lv_type_0_14= Process | lv_type_0_15= Subprogram_group | lv_type_0_16= Subprogram | lv_type_0_17= KW_System | lv_type_0_18= Thread_group | lv_type_0_19= Thread | lv_type_0_20= Virtual_bus | lv_type_0_21= Virtual_processor | lv_type_0_22= Connection | lv_type_0_23= Property | lv_type_0_24= Feature | lv_type_0_25= Port | lv_type_0_26= Data_port | lv_type_0_27= Event_port | lv_type_0_28= Event_data_port | lv_type_0_29= Feature_group | lv_type_0_30= Access | lv_type_0_31= Bus_access | lv_type_0_32= Provides_bus_access | lv_type_0_33= Requires_bus_access | lv_type_0_34= Data_access | lv_type_0_35= Provides_data_access | lv_type_0_36= Requires_data_access | lv_type_0_37= Subprogram_access | lv_type_0_38= Provides_subprogram_access | lv_type_0_39= Requires_subprogram_access | lv_type_0_40= Subprogram_group_access | lv_type_0_41= Provides_subprogram_group_access | lv_type_0_42= Requires_subprogram_group_access | lv_type_0_43= Flow_specification | lv_type_0_44= End_to_end_flow ) )
            {
            // InternalResoluteParser.g:384:1: ( (lv_type_0_1= Int | lv_type_0_2= Real | lv_type_0_3= String | lv_type_0_4= Bool | lv_type_0_5= Range | lv_type_0_6= Aadl | lv_type_0_7= Component | lv_type_0_8= Abstract | lv_type_0_9= Bus | lv_type_0_10= Data | lv_type_0_11= Device | lv_type_0_12= Memory | lv_type_0_13= Processor | lv_type_0_14= Process | lv_type_0_15= Subprogram_group | lv_type_0_16= Subprogram | lv_type_0_17= KW_System | lv_type_0_18= Thread_group | lv_type_0_19= Thread | lv_type_0_20= Virtual_bus | lv_type_0_21= Virtual_processor | lv_type_0_22= Connection | lv_type_0_23= Property | lv_type_0_24= Feature | lv_type_0_25= Port | lv_type_0_26= Data_port | lv_type_0_27= Event_port | lv_type_0_28= Event_data_port | lv_type_0_29= Feature_group | lv_type_0_30= Access | lv_type_0_31= Bus_access | lv_type_0_32= Provides_bus_access | lv_type_0_33= Requires_bus_access | lv_type_0_34= Data_access | lv_type_0_35= Provides_data_access | lv_type_0_36= Requires_data_access | lv_type_0_37= Subprogram_access | lv_type_0_38= Provides_subprogram_access | lv_type_0_39= Requires_subprogram_access | lv_type_0_40= Subprogram_group_access | lv_type_0_41= Provides_subprogram_group_access | lv_type_0_42= Requires_subprogram_group_access | lv_type_0_43= Flow_specification | lv_type_0_44= End_to_end_flow ) )
            // InternalResoluteParser.g:385:1: (lv_type_0_1= Int | lv_type_0_2= Real | lv_type_0_3= String | lv_type_0_4= Bool | lv_type_0_5= Range | lv_type_0_6= Aadl | lv_type_0_7= Component | lv_type_0_8= Abstract | lv_type_0_9= Bus | lv_type_0_10= Data | lv_type_0_11= Device | lv_type_0_12= Memory | lv_type_0_13= Processor | lv_type_0_14= Process | lv_type_0_15= Subprogram_group | lv_type_0_16= Subprogram | lv_type_0_17= KW_System | lv_type_0_18= Thread_group | lv_type_0_19= Thread | lv_type_0_20= Virtual_bus | lv_type_0_21= Virtual_processor | lv_type_0_22= Connection | lv_type_0_23= Property | lv_type_0_24= Feature | lv_type_0_25= Port | lv_type_0_26= Data_port | lv_type_0_27= Event_port | lv_type_0_28= Event_data_port | lv_type_0_29= Feature_group | lv_type_0_30= Access | lv_type_0_31= Bus_access | lv_type_0_32= Provides_bus_access | lv_type_0_33= Requires_bus_access | lv_type_0_34= Data_access | lv_type_0_35= Provides_data_access | lv_type_0_36= Requires_data_access | lv_type_0_37= Subprogram_access | lv_type_0_38= Provides_subprogram_access | lv_type_0_39= Requires_subprogram_access | lv_type_0_40= Subprogram_group_access | lv_type_0_41= Provides_subprogram_group_access | lv_type_0_42= Requires_subprogram_group_access | lv_type_0_43= Flow_specification | lv_type_0_44= End_to_end_flow )
            {
            // InternalResoluteParser.g:385:1: (lv_type_0_1= Int | lv_type_0_2= Real | lv_type_0_3= String | lv_type_0_4= Bool | lv_type_0_5= Range | lv_type_0_6= Aadl | lv_type_0_7= Component | lv_type_0_8= Abstract | lv_type_0_9= Bus | lv_type_0_10= Data | lv_type_0_11= Device | lv_type_0_12= Memory | lv_type_0_13= Processor | lv_type_0_14= Process | lv_type_0_15= Subprogram_group | lv_type_0_16= Subprogram | lv_type_0_17= KW_System | lv_type_0_18= Thread_group | lv_type_0_19= Thread | lv_type_0_20= Virtual_bus | lv_type_0_21= Virtual_processor | lv_type_0_22= Connection | lv_type_0_23= Property | lv_type_0_24= Feature | lv_type_0_25= Port | lv_type_0_26= Data_port | lv_type_0_27= Event_port | lv_type_0_28= Event_data_port | lv_type_0_29= Feature_group | lv_type_0_30= Access | lv_type_0_31= Bus_access | lv_type_0_32= Provides_bus_access | lv_type_0_33= Requires_bus_access | lv_type_0_34= Data_access | lv_type_0_35= Provides_data_access | lv_type_0_36= Requires_data_access | lv_type_0_37= Subprogram_access | lv_type_0_38= Provides_subprogram_access | lv_type_0_39= Requires_subprogram_access | lv_type_0_40= Subprogram_group_access | lv_type_0_41= Provides_subprogram_group_access | lv_type_0_42= Requires_subprogram_group_access | lv_type_0_43= Flow_specification | lv_type_0_44= End_to_end_flow )
            int alt5=44;
            switch ( input.LA(1) ) {
            case Int:
                {
                alt5=1;
                }
                break;
            case Real:
                {
                alt5=2;
                }
                break;
            case String:
                {
                alt5=3;
                }
                break;
            case Bool:
                {
                alt5=4;
                }
                break;
            case Range:
                {
                alt5=5;
                }
                break;
            case Aadl:
                {
                alt5=6;
                }
                break;
            case Component:
                {
                alt5=7;
                }
                break;
            case Abstract:
                {
                alt5=8;
                }
                break;
            case Bus:
                {
                alt5=9;
                }
                break;
            case Data:
                {
                alt5=10;
                }
                break;
            case Device:
                {
                alt5=11;
                }
                break;
            case Memory:
                {
                alt5=12;
                }
                break;
            case Processor:
                {
                alt5=13;
                }
                break;
            case Process:
                {
                alt5=14;
                }
                break;
            case Subprogram_group:
                {
                alt5=15;
                }
                break;
            case Subprogram:
                {
                alt5=16;
                }
                break;
            case KW_System:
                {
                alt5=17;
                }
                break;
            case Thread_group:
                {
                alt5=18;
                }
                break;
            case Thread:
                {
                alt5=19;
                }
                break;
            case Virtual_bus:
                {
                alt5=20;
                }
                break;
            case Virtual_processor:
                {
                alt5=21;
                }
                break;
            case Connection:
                {
                alt5=22;
                }
                break;
            case Property:
                {
                alt5=23;
                }
                break;
            case Feature:
                {
                alt5=24;
                }
                break;
            case Port:
                {
                alt5=25;
                }
                break;
            case Data_port:
                {
                alt5=26;
                }
                break;
            case Event_port:
                {
                alt5=27;
                }
                break;
            case Event_data_port:
                {
                alt5=28;
                }
                break;
            case Feature_group:
                {
                alt5=29;
                }
                break;
            case Access:
                {
                alt5=30;
                }
                break;
            case Bus_access:
                {
                alt5=31;
                }
                break;
            case Provides_bus_access:
                {
                alt5=32;
                }
                break;
            case Requires_bus_access:
                {
                alt5=33;
                }
                break;
            case Data_access:
                {
                alt5=34;
                }
                break;
            case Provides_data_access:
                {
                alt5=35;
                }
                break;
            case Requires_data_access:
                {
                alt5=36;
                }
                break;
            case Subprogram_access:
                {
                alt5=37;
                }
                break;
            case Provides_subprogram_access:
                {
                alt5=38;
                }
                break;
            case Requires_subprogram_access:
                {
                alt5=39;
                }
                break;
            case Subprogram_group_access:
                {
                alt5=40;
                }
                break;
            case Provides_subprogram_group_access:
                {
                alt5=41;
                }
                break;
            case Requires_subprogram_group_access:
                {
                alt5=42;
                }
                break;
            case Flow_specification:
                {
                alt5=43;
                }
                break;
            case End_to_end_flow:
                {
                alt5=44;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // InternalResoluteParser.g:386:3: lv_type_0_1= Int
                    {
                    lv_type_0_1=(Token)match(input,Int,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_1, grammarAccess.getBaseTypeAccess().getTypeIntKeyword_0_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_1, null);
                      	    
                    }

                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:399:8: lv_type_0_2= Real
                    {
                    lv_type_0_2=(Token)match(input,Real,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_2, grammarAccess.getBaseTypeAccess().getTypeRealKeyword_0_1());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_2, null);
                      	    
                    }

                    }
                    break;
                case 3 :
                    // InternalResoluteParser.g:412:8: lv_type_0_3= String
                    {
                    lv_type_0_3=(Token)match(input,String,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_3, grammarAccess.getBaseTypeAccess().getTypeStringKeyword_0_2());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_3, null);
                      	    
                    }

                    }
                    break;
                case 4 :
                    // InternalResoluteParser.g:425:8: lv_type_0_4= Bool
                    {
                    lv_type_0_4=(Token)match(input,Bool,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_4, grammarAccess.getBaseTypeAccess().getTypeBoolKeyword_0_3());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_4, null);
                      	    
                    }

                    }
                    break;
                case 5 :
                    // InternalResoluteParser.g:438:8: lv_type_0_5= Range
                    {
                    lv_type_0_5=(Token)match(input,Range,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_5, grammarAccess.getBaseTypeAccess().getTypeRangeKeyword_0_4());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_5, null);
                      	    
                    }

                    }
                    break;
                case 6 :
                    // InternalResoluteParser.g:451:8: lv_type_0_6= Aadl
                    {
                    lv_type_0_6=(Token)match(input,Aadl,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_6, grammarAccess.getBaseTypeAccess().getTypeAadlKeyword_0_5());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_6, null);
                      	    
                    }

                    }
                    break;
                case 7 :
                    // InternalResoluteParser.g:464:8: lv_type_0_7= Component
                    {
                    lv_type_0_7=(Token)match(input,Component,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_7, grammarAccess.getBaseTypeAccess().getTypeComponentKeyword_0_6());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_7, null);
                      	    
                    }

                    }
                    break;
                case 8 :
                    // InternalResoluteParser.g:477:8: lv_type_0_8= Abstract
                    {
                    lv_type_0_8=(Token)match(input,Abstract,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_8, grammarAccess.getBaseTypeAccess().getTypeAbstractKeyword_0_7());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_8, null);
                      	    
                    }

                    }
                    break;
                case 9 :
                    // InternalResoluteParser.g:490:8: lv_type_0_9= Bus
                    {
                    lv_type_0_9=(Token)match(input,Bus,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_9, grammarAccess.getBaseTypeAccess().getTypeBusKeyword_0_8());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_9, null);
                      	    
                    }

                    }
                    break;
                case 10 :
                    // InternalResoluteParser.g:503:8: lv_type_0_10= Data
                    {
                    lv_type_0_10=(Token)match(input,Data,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_10, grammarAccess.getBaseTypeAccess().getTypeDataKeyword_0_9());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_10, null);
                      	    
                    }

                    }
                    break;
                case 11 :
                    // InternalResoluteParser.g:516:8: lv_type_0_11= Device
                    {
                    lv_type_0_11=(Token)match(input,Device,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_11, grammarAccess.getBaseTypeAccess().getTypeDeviceKeyword_0_10());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_11, null);
                      	    
                    }

                    }
                    break;
                case 12 :
                    // InternalResoluteParser.g:529:8: lv_type_0_12= Memory
                    {
                    lv_type_0_12=(Token)match(input,Memory,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_12, grammarAccess.getBaseTypeAccess().getTypeMemoryKeyword_0_11());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_12, null);
                      	    
                    }

                    }
                    break;
                case 13 :
                    // InternalResoluteParser.g:542:8: lv_type_0_13= Processor
                    {
                    lv_type_0_13=(Token)match(input,Processor,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_13, grammarAccess.getBaseTypeAccess().getTypeProcessorKeyword_0_12());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_13, null);
                      	    
                    }

                    }
                    break;
                case 14 :
                    // InternalResoluteParser.g:555:8: lv_type_0_14= Process
                    {
                    lv_type_0_14=(Token)match(input,Process,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_14, grammarAccess.getBaseTypeAccess().getTypeProcessKeyword_0_13());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_14, null);
                      	    
                    }

                    }
                    break;
                case 15 :
                    // InternalResoluteParser.g:568:8: lv_type_0_15= Subprogram_group
                    {
                    lv_type_0_15=(Token)match(input,Subprogram_group,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_15, grammarAccess.getBaseTypeAccess().getTypeSubprogram_groupKeyword_0_14());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_15, null);
                      	    
                    }

                    }
                    break;
                case 16 :
                    // InternalResoluteParser.g:581:8: lv_type_0_16= Subprogram
                    {
                    lv_type_0_16=(Token)match(input,Subprogram,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_16, grammarAccess.getBaseTypeAccess().getTypeSubprogramKeyword_0_15());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_16, null);
                      	    
                    }

                    }
                    break;
                case 17 :
                    // InternalResoluteParser.g:594:8: lv_type_0_17= KW_System
                    {
                    lv_type_0_17=(Token)match(input,KW_System,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_17, grammarAccess.getBaseTypeAccess().getTypeSystemKeyword_0_16());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_17, null);
                      	    
                    }

                    }
                    break;
                case 18 :
                    // InternalResoluteParser.g:607:8: lv_type_0_18= Thread_group
                    {
                    lv_type_0_18=(Token)match(input,Thread_group,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_18, grammarAccess.getBaseTypeAccess().getTypeThread_groupKeyword_0_17());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_18, null);
                      	    
                    }

                    }
                    break;
                case 19 :
                    // InternalResoluteParser.g:620:8: lv_type_0_19= Thread
                    {
                    lv_type_0_19=(Token)match(input,Thread,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_19, grammarAccess.getBaseTypeAccess().getTypeThreadKeyword_0_18());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_19, null);
                      	    
                    }

                    }
                    break;
                case 20 :
                    // InternalResoluteParser.g:633:8: lv_type_0_20= Virtual_bus
                    {
                    lv_type_0_20=(Token)match(input,Virtual_bus,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_20, grammarAccess.getBaseTypeAccess().getTypeVirtual_busKeyword_0_19());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_20, null);
                      	    
                    }

                    }
                    break;
                case 21 :
                    // InternalResoluteParser.g:646:8: lv_type_0_21= Virtual_processor
                    {
                    lv_type_0_21=(Token)match(input,Virtual_processor,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_21, grammarAccess.getBaseTypeAccess().getTypeVirtual_processorKeyword_0_20());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_21, null);
                      	    
                    }

                    }
                    break;
                case 22 :
                    // InternalResoluteParser.g:659:8: lv_type_0_22= Connection
                    {
                    lv_type_0_22=(Token)match(input,Connection,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_22, grammarAccess.getBaseTypeAccess().getTypeConnectionKeyword_0_21());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_22, null);
                      	    
                    }

                    }
                    break;
                case 23 :
                    // InternalResoluteParser.g:672:8: lv_type_0_23= Property
                    {
                    lv_type_0_23=(Token)match(input,Property,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_23, grammarAccess.getBaseTypeAccess().getTypePropertyKeyword_0_22());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_23, null);
                      	    
                    }

                    }
                    break;
                case 24 :
                    // InternalResoluteParser.g:685:8: lv_type_0_24= Feature
                    {
                    lv_type_0_24=(Token)match(input,Feature,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_24, grammarAccess.getBaseTypeAccess().getTypeFeatureKeyword_0_23());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_24, null);
                      	    
                    }

                    }
                    break;
                case 25 :
                    // InternalResoluteParser.g:698:8: lv_type_0_25= Port
                    {
                    lv_type_0_25=(Token)match(input,Port,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_25, grammarAccess.getBaseTypeAccess().getTypePortKeyword_0_24());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_25, null);
                      	    
                    }

                    }
                    break;
                case 26 :
                    // InternalResoluteParser.g:711:8: lv_type_0_26= Data_port
                    {
                    lv_type_0_26=(Token)match(input,Data_port,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_26, grammarAccess.getBaseTypeAccess().getTypeData_portKeyword_0_25());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_26, null);
                      	    
                    }

                    }
                    break;
                case 27 :
                    // InternalResoluteParser.g:724:8: lv_type_0_27= Event_port
                    {
                    lv_type_0_27=(Token)match(input,Event_port,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_27, grammarAccess.getBaseTypeAccess().getTypeEvent_portKeyword_0_26());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_27, null);
                      	    
                    }

                    }
                    break;
                case 28 :
                    // InternalResoluteParser.g:737:8: lv_type_0_28= Event_data_port
                    {
                    lv_type_0_28=(Token)match(input,Event_data_port,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_28, grammarAccess.getBaseTypeAccess().getTypeEvent_data_portKeyword_0_27());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_28, null);
                      	    
                    }

                    }
                    break;
                case 29 :
                    // InternalResoluteParser.g:750:8: lv_type_0_29= Feature_group
                    {
                    lv_type_0_29=(Token)match(input,Feature_group,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_29, grammarAccess.getBaseTypeAccess().getTypeFeature_groupKeyword_0_28());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_29, null);
                      	    
                    }

                    }
                    break;
                case 30 :
                    // InternalResoluteParser.g:763:8: lv_type_0_30= Access
                    {
                    lv_type_0_30=(Token)match(input,Access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_30, grammarAccess.getBaseTypeAccess().getTypeAccessKeyword_0_29());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_30, null);
                      	    
                    }

                    }
                    break;
                case 31 :
                    // InternalResoluteParser.g:776:8: lv_type_0_31= Bus_access
                    {
                    lv_type_0_31=(Token)match(input,Bus_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_31, grammarAccess.getBaseTypeAccess().getTypeBus_accessKeyword_0_30());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_31, null);
                      	    
                    }

                    }
                    break;
                case 32 :
                    // InternalResoluteParser.g:789:8: lv_type_0_32= Provides_bus_access
                    {
                    lv_type_0_32=(Token)match(input,Provides_bus_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_32, grammarAccess.getBaseTypeAccess().getTypeProvides_bus_accessKeyword_0_31());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_32, null);
                      	    
                    }

                    }
                    break;
                case 33 :
                    // InternalResoluteParser.g:802:8: lv_type_0_33= Requires_bus_access
                    {
                    lv_type_0_33=(Token)match(input,Requires_bus_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_33, grammarAccess.getBaseTypeAccess().getTypeRequires_bus_accessKeyword_0_32());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_33, null);
                      	    
                    }

                    }
                    break;
                case 34 :
                    // InternalResoluteParser.g:815:8: lv_type_0_34= Data_access
                    {
                    lv_type_0_34=(Token)match(input,Data_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_34, grammarAccess.getBaseTypeAccess().getTypeData_accessKeyword_0_33());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_34, null);
                      	    
                    }

                    }
                    break;
                case 35 :
                    // InternalResoluteParser.g:828:8: lv_type_0_35= Provides_data_access
                    {
                    lv_type_0_35=(Token)match(input,Provides_data_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_35, grammarAccess.getBaseTypeAccess().getTypeProvides_data_accessKeyword_0_34());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_35, null);
                      	    
                    }

                    }
                    break;
                case 36 :
                    // InternalResoluteParser.g:841:8: lv_type_0_36= Requires_data_access
                    {
                    lv_type_0_36=(Token)match(input,Requires_data_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_36, grammarAccess.getBaseTypeAccess().getTypeRequires_data_accessKeyword_0_35());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_36, null);
                      	    
                    }

                    }
                    break;
                case 37 :
                    // InternalResoluteParser.g:854:8: lv_type_0_37= Subprogram_access
                    {
                    lv_type_0_37=(Token)match(input,Subprogram_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_37, grammarAccess.getBaseTypeAccess().getTypeSubprogram_accessKeyword_0_36());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_37, null);
                      	    
                    }

                    }
                    break;
                case 38 :
                    // InternalResoluteParser.g:867:8: lv_type_0_38= Provides_subprogram_access
                    {
                    lv_type_0_38=(Token)match(input,Provides_subprogram_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_38, grammarAccess.getBaseTypeAccess().getTypeProvides_subprogram_accessKeyword_0_37());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_38, null);
                      	    
                    }

                    }
                    break;
                case 39 :
                    // InternalResoluteParser.g:880:8: lv_type_0_39= Requires_subprogram_access
                    {
                    lv_type_0_39=(Token)match(input,Requires_subprogram_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_39, grammarAccess.getBaseTypeAccess().getTypeRequires_subprogram_accessKeyword_0_38());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_39, null);
                      	    
                    }

                    }
                    break;
                case 40 :
                    // InternalResoluteParser.g:893:8: lv_type_0_40= Subprogram_group_access
                    {
                    lv_type_0_40=(Token)match(input,Subprogram_group_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_40, grammarAccess.getBaseTypeAccess().getTypeSubprogram_group_accessKeyword_0_39());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_40, null);
                      	    
                    }

                    }
                    break;
                case 41 :
                    // InternalResoluteParser.g:906:8: lv_type_0_41= Provides_subprogram_group_access
                    {
                    lv_type_0_41=(Token)match(input,Provides_subprogram_group_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_41, grammarAccess.getBaseTypeAccess().getTypeProvides_subprogram_group_accessKeyword_0_40());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_41, null);
                      	    
                    }

                    }
                    break;
                case 42 :
                    // InternalResoluteParser.g:919:8: lv_type_0_42= Requires_subprogram_group_access
                    {
                    lv_type_0_42=(Token)match(input,Requires_subprogram_group_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_42, grammarAccess.getBaseTypeAccess().getTypeRequires_subprogram_group_accessKeyword_0_41());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_42, null);
                      	    
                    }

                    }
                    break;
                case 43 :
                    // InternalResoluteParser.g:932:8: lv_type_0_43= Flow_specification
                    {
                    lv_type_0_43=(Token)match(input,Flow_specification,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_43, grammarAccess.getBaseTypeAccess().getTypeFlow_specificationKeyword_0_42());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_43, null);
                      	    
                    }

                    }
                    break;
                case 44 :
                    // InternalResoluteParser.g:945:8: lv_type_0_44= End_to_end_flow
                    {
                    lv_type_0_44=(Token)match(input,End_to_end_flow,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_type_0_44, grammarAccess.getBaseTypeAccess().getTypeEnd_to_end_flowKeyword_0_43());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBaseTypeRule());
                      	        }
                             		setWithLastConsumed(current, "type", lv_type_0_44, null);
                      	    
                    }

                    }
                    break;

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBaseType"


    // $ANTLR start "entryRuleArg"
    // InternalResoluteParser.g:969:1: entryRuleArg returns [EObject current=null] : iv_ruleArg= ruleArg EOF ;
    public final EObject entryRuleArg() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArg = null;


        try {
            // InternalResoluteParser.g:970:2: (iv_ruleArg= ruleArg EOF )
            // InternalResoluteParser.g:971:2: iv_ruleArg= ruleArg EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getArgRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleArg=ruleArg();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleArg; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleArg"


    // $ANTLR start "ruleArg"
    // InternalResoluteParser.g:978:1: ruleArg returns [EObject current=null] : ( ( ( ( ( ( RULE_ID ) ) Colon ( ( ruleType ) ) ) )=> ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) ) ) | ( () ( (lv_name_4_0= RULE_ID ) ) otherlv_5= Colon ( (lv_expr_6_0= ruleExpr ) ) ) ) ;
    public final EObject ruleArg() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token lv_name_4_0=null;
        Token otherlv_5=null;
        EObject lv_type_2_0 = null;

        EObject lv_expr_6_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:981:28: ( ( ( ( ( ( ( RULE_ID ) ) Colon ( ( ruleType ) ) ) )=> ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) ) ) | ( () ( (lv_name_4_0= RULE_ID ) ) otherlv_5= Colon ( (lv_expr_6_0= ruleExpr ) ) ) ) )
            // InternalResoluteParser.g:982:1: ( ( ( ( ( ( RULE_ID ) ) Colon ( ( ruleType ) ) ) )=> ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) ) ) | ( () ( (lv_name_4_0= RULE_ID ) ) otherlv_5= Colon ( (lv_expr_6_0= ruleExpr ) ) ) )
            {
            // InternalResoluteParser.g:982:1: ( ( ( ( ( ( RULE_ID ) ) Colon ( ( ruleType ) ) ) )=> ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) ) ) | ( () ( (lv_name_4_0= RULE_ID ) ) otherlv_5= Colon ( (lv_expr_6_0= ruleExpr ) ) ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RULE_ID) ) {
                int LA6_1 = input.LA(2);

                if ( (synpred1_InternalResoluteParser()) ) {
                    alt6=1;
                }
                else if ( (true) ) {
                    alt6=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // InternalResoluteParser.g:982:2: ( ( ( ( ( RULE_ID ) ) Colon ( ( ruleType ) ) ) )=> ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) ) )
                    {
                    // InternalResoluteParser.g:982:2: ( ( ( ( ( RULE_ID ) ) Colon ( ( ruleType ) ) ) )=> ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) ) )
                    // InternalResoluteParser.g:982:3: ( ( ( ( RULE_ID ) ) Colon ( ( ruleType ) ) ) )=> ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) )
                    {
                    // InternalResoluteParser.g:993:6: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) )
                    // InternalResoluteParser.g:993:7: ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) )
                    {
                    // InternalResoluteParser.g:993:7: ( (lv_name_0_0= RULE_ID ) )
                    // InternalResoluteParser.g:994:1: (lv_name_0_0= RULE_ID )
                    {
                    // InternalResoluteParser.g:994:1: (lv_name_0_0= RULE_ID )
                    // InternalResoluteParser.g:995:3: lv_name_0_0= RULE_ID
                    {
                    lv_name_0_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_11); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_0_0, grammarAccess.getArgAccess().getNameIDTerminalRuleCall_0_0_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getArgRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_0_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.ID");
                      	    
                    }

                    }


                    }

                    otherlv_1=(Token)match(input,Colon,FollowSets000.FOLLOW_4); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getArgAccess().getColonKeyword_0_0_1());
                          
                    }
                    // InternalResoluteParser.g:1016:1: ( (lv_type_2_0= ruleType ) )
                    // InternalResoluteParser.g:1017:1: (lv_type_2_0= ruleType )
                    {
                    // InternalResoluteParser.g:1017:1: (lv_type_2_0= ruleType )
                    // InternalResoluteParser.g:1018:3: lv_type_2_0= ruleType
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getArgAccess().getTypeTypeParserRuleCall_0_0_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_type_2_0=ruleType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getArgRule());
                      	        }
                             		set(
                             			current, 
                             			"type",
                              		lv_type_2_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Type");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:1035:6: ( () ( (lv_name_4_0= RULE_ID ) ) otherlv_5= Colon ( (lv_expr_6_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:1035:6: ( () ( (lv_name_4_0= RULE_ID ) ) otherlv_5= Colon ( (lv_expr_6_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:1035:7: () ( (lv_name_4_0= RULE_ID ) ) otherlv_5= Colon ( (lv_expr_6_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:1035:7: ()
                    // InternalResoluteParser.g:1036:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getArgAccess().getQuantArgAction_1_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:1041:2: ( (lv_name_4_0= RULE_ID ) )
                    // InternalResoluteParser.g:1042:1: (lv_name_4_0= RULE_ID )
                    {
                    // InternalResoluteParser.g:1042:1: (lv_name_4_0= RULE_ID )
                    // InternalResoluteParser.g:1043:3: lv_name_4_0= RULE_ID
                    {
                    lv_name_4_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_11); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_4_0, grammarAccess.getArgAccess().getNameIDTerminalRuleCall_1_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getArgRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_4_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.ID");
                      	    
                    }

                    }


                    }

                    otherlv_5=(Token)match(input,Colon,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_5, grammarAccess.getArgAccess().getColonKeyword_1_2());
                          
                    }
                    // InternalResoluteParser.g:1064:1: ( (lv_expr_6_0= ruleExpr ) )
                    // InternalResoluteParser.g:1065:1: (lv_expr_6_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:1065:1: (lv_expr_6_0= ruleExpr )
                    // InternalResoluteParser.g:1066:3: lv_expr_6_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getArgAccess().getExprExprParserRuleCall_1_3_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_6_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getArgRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_6_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleArg"


    // $ANTLR start "entryRuleConstantDefinition"
    // InternalResoluteParser.g:1090:1: entryRuleConstantDefinition returns [EObject current=null] : iv_ruleConstantDefinition= ruleConstantDefinition EOF ;
    public final EObject entryRuleConstantDefinition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConstantDefinition = null;


        try {
            // InternalResoluteParser.g:1091:2: (iv_ruleConstantDefinition= ruleConstantDefinition EOF )
            // InternalResoluteParser.g:1092:2: iv_ruleConstantDefinition= ruleConstantDefinition EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConstantDefinitionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleConstantDefinition=ruleConstantDefinition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConstantDefinition; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleConstantDefinition"


    // $ANTLR start "ruleConstantDefinition"
    // InternalResoluteParser.g:1099:1: ruleConstantDefinition returns [EObject current=null] : ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) ) ;
    public final EObject ruleConstantDefinition() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_type_2_0 = null;

        EObject lv_expr_4_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1102:28: ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) ) )
            // InternalResoluteParser.g:1103:1: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) )
            {
            // InternalResoluteParser.g:1103:1: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) )
            // InternalResoluteParser.g:1103:2: ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) )
            {
            // InternalResoluteParser.g:1103:2: ( (lv_name_0_0= RULE_ID ) )
            // InternalResoluteParser.g:1104:1: (lv_name_0_0= RULE_ID )
            {
            // InternalResoluteParser.g:1104:1: (lv_name_0_0= RULE_ID )
            // InternalResoluteParser.g:1105:3: lv_name_0_0= RULE_ID
            {
            lv_name_0_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_11); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_0_0, grammarAccess.getConstantDefinitionAccess().getNameIDTerminalRuleCall_0_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getConstantDefinitionRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_0_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.ID");
              	    
            }

            }


            }

            otherlv_1=(Token)match(input,Colon,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getConstantDefinitionAccess().getColonKeyword_1());
                  
            }
            // InternalResoluteParser.g:1126:1: ( (lv_type_2_0= ruleType ) )
            // InternalResoluteParser.g:1127:1: (lv_type_2_0= ruleType )
            {
            // InternalResoluteParser.g:1127:1: (lv_type_2_0= ruleType )
            // InternalResoluteParser.g:1128:3: lv_type_2_0= ruleType
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getConstantDefinitionAccess().getTypeTypeParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_13);
            lv_type_2_0=ruleType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getConstantDefinitionRule());
              	        }
                     		set(
                     			current, 
                     			"type",
                      		lv_type_2_0, 
                      		"com.rockwellcollins.atc.resolute.Resolute.Type");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            otherlv_3=(Token)match(input,EqualsSign,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_3, grammarAccess.getConstantDefinitionAccess().getEqualsSignKeyword_3());
                  
            }
            // InternalResoluteParser.g:1149:1: ( (lv_expr_4_0= ruleExpr ) )
            // InternalResoluteParser.g:1150:1: (lv_expr_4_0= ruleExpr )
            {
            // InternalResoluteParser.g:1150:1: (lv_expr_4_0= ruleExpr )
            // InternalResoluteParser.g:1151:3: lv_expr_4_0= ruleExpr
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getConstantDefinitionAccess().getExprExprParserRuleCall_4_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_expr_4_0=ruleExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getConstantDefinitionRule());
              	        }
                     		set(
                     			current, 
                     			"expr",
                      		lv_expr_4_0, 
                      		"com.rockwellcollins.atc.resolute.Resolute.Expr");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleConstantDefinition"


    // $ANTLR start "entryRuleFunctionDefinition"
    // InternalResoluteParser.g:1175:1: entryRuleFunctionDefinition returns [EObject current=null] : iv_ruleFunctionDefinition= ruleFunctionDefinition EOF ;
    public final EObject entryRuleFunctionDefinition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFunctionDefinition = null;


        try {
            // InternalResoluteParser.g:1176:2: (iv_ruleFunctionDefinition= ruleFunctionDefinition EOF )
            // InternalResoluteParser.g:1177:2: iv_ruleFunctionDefinition= ruleFunctionDefinition EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFunctionDefinitionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleFunctionDefinition=ruleFunctionDefinition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFunctionDefinition; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleFunctionDefinition"


    // $ANTLR start "ruleFunctionDefinition"
    // InternalResoluteParser.g:1184:1: ruleFunctionDefinition returns [EObject current=null] : ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= LeftParenthesis ( ( (lv_args_2_0= ruleArg ) ) (otherlv_3= Comma ( (lv_args_4_0= ruleArg ) ) )* )? otherlv_5= RightParenthesis ( (lv_body_6_0= ruleDefinitionBody ) ) ) ;
    public final EObject ruleFunctionDefinition() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_args_2_0 = null;

        EObject lv_args_4_0 = null;

        EObject lv_body_6_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1187:28: ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= LeftParenthesis ( ( (lv_args_2_0= ruleArg ) ) (otherlv_3= Comma ( (lv_args_4_0= ruleArg ) ) )* )? otherlv_5= RightParenthesis ( (lv_body_6_0= ruleDefinitionBody ) ) ) )
            // InternalResoluteParser.g:1188:1: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= LeftParenthesis ( ( (lv_args_2_0= ruleArg ) ) (otherlv_3= Comma ( (lv_args_4_0= ruleArg ) ) )* )? otherlv_5= RightParenthesis ( (lv_body_6_0= ruleDefinitionBody ) ) )
            {
            // InternalResoluteParser.g:1188:1: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= LeftParenthesis ( ( (lv_args_2_0= ruleArg ) ) (otherlv_3= Comma ( (lv_args_4_0= ruleArg ) ) )* )? otherlv_5= RightParenthesis ( (lv_body_6_0= ruleDefinitionBody ) ) )
            // InternalResoluteParser.g:1188:2: ( (lv_name_0_0= RULE_ID ) ) otherlv_1= LeftParenthesis ( ( (lv_args_2_0= ruleArg ) ) (otherlv_3= Comma ( (lv_args_4_0= ruleArg ) ) )* )? otherlv_5= RightParenthesis ( (lv_body_6_0= ruleDefinitionBody ) )
            {
            // InternalResoluteParser.g:1188:2: ( (lv_name_0_0= RULE_ID ) )
            // InternalResoluteParser.g:1189:1: (lv_name_0_0= RULE_ID )
            {
            // InternalResoluteParser.g:1189:1: (lv_name_0_0= RULE_ID )
            // InternalResoluteParser.g:1190:3: lv_name_0_0= RULE_ID
            {
            lv_name_0_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_14); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_0_0, grammarAccess.getFunctionDefinitionAccess().getNameIDTerminalRuleCall_0_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getFunctionDefinitionRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_0_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.ID");
              	    
            }

            }


            }

            otherlv_1=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getFunctionDefinitionAccess().getLeftParenthesisKeyword_1());
                  
            }
            // InternalResoluteParser.g:1211:1: ( ( (lv_args_2_0= ruleArg ) ) (otherlv_3= Comma ( (lv_args_4_0= ruleArg ) ) )* )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_ID) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // InternalResoluteParser.g:1211:2: ( (lv_args_2_0= ruleArg ) ) (otherlv_3= Comma ( (lv_args_4_0= ruleArg ) ) )*
                    {
                    // InternalResoluteParser.g:1211:2: ( (lv_args_2_0= ruleArg ) )
                    // InternalResoluteParser.g:1212:1: (lv_args_2_0= ruleArg )
                    {
                    // InternalResoluteParser.g:1212:1: (lv_args_2_0= ruleArg )
                    // InternalResoluteParser.g:1213:3: lv_args_2_0= ruleArg
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getFunctionDefinitionAccess().getArgsArgParserRuleCall_2_0_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_16);
                    lv_args_2_0=ruleArg();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getFunctionDefinitionRule());
                      	        }
                             		add(
                             			current, 
                             			"args",
                              		lv_args_2_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Arg");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // InternalResoluteParser.g:1229:2: (otherlv_3= Comma ( (lv_args_4_0= ruleArg ) ) )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==Comma) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // InternalResoluteParser.g:1230:2: otherlv_3= Comma ( (lv_args_4_0= ruleArg ) )
                    	    {
                    	    otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	          	newLeafNode(otherlv_3, grammarAccess.getFunctionDefinitionAccess().getCommaKeyword_2_1_0());
                    	          
                    	    }
                    	    // InternalResoluteParser.g:1234:1: ( (lv_args_4_0= ruleArg ) )
                    	    // InternalResoluteParser.g:1235:1: (lv_args_4_0= ruleArg )
                    	    {
                    	    // InternalResoluteParser.g:1235:1: (lv_args_4_0= ruleArg )
                    	    // InternalResoluteParser.g:1236:3: lv_args_4_0= ruleArg
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getFunctionDefinitionAccess().getArgsArgParserRuleCall_2_1_1_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_16);
                    	    lv_args_4_0=ruleArg();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getFunctionDefinitionRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"args",
                    	              		lv_args_4_0, 
                    	              		"com.rockwellcollins.atc.resolute.Resolute.Arg");
                    	      	        afterParserOrEnumRuleCall();
                    	      	    
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_5=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_17); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_5, grammarAccess.getFunctionDefinitionAccess().getRightParenthesisKeyword_3());
                  
            }
            // InternalResoluteParser.g:1257:1: ( (lv_body_6_0= ruleDefinitionBody ) )
            // InternalResoluteParser.g:1258:1: (lv_body_6_0= ruleDefinitionBody )
            {
            // InternalResoluteParser.g:1258:1: (lv_body_6_0= ruleDefinitionBody )
            // InternalResoluteParser.g:1259:3: lv_body_6_0= ruleDefinitionBody
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getFunctionDefinitionAccess().getBodyDefinitionBodyParserRuleCall_4_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_body_6_0=ruleDefinitionBody();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getFunctionDefinitionRule());
              	        }
                     		set(
                     			current, 
                     			"body",
                      		lv_body_6_0, 
                      		"com.rockwellcollins.atc.resolute.Resolute.DefinitionBody");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleFunctionDefinition"


    // $ANTLR start "entryRuleDefinitionBody"
    // InternalResoluteParser.g:1283:1: entryRuleDefinitionBody returns [EObject current=null] : iv_ruleDefinitionBody= ruleDefinitionBody EOF ;
    public final EObject entryRuleDefinitionBody() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefinitionBody = null;


        try {
            // InternalResoluteParser.g:1284:2: (iv_ruleDefinitionBody= ruleDefinitionBody EOF )
            // InternalResoluteParser.g:1285:2: iv_ruleDefinitionBody= ruleDefinitionBody EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefinitionBodyRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDefinitionBody=ruleDefinitionBody();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefinitionBody; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDefinitionBody"


    // $ANTLR start "ruleDefinitionBody"
    // InternalResoluteParser.g:1292:1: ruleDefinitionBody returns [EObject current=null] : ( ( () otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) ) | ( () otherlv_6= LessThanSignEqualsSign (otherlv_7= AsteriskAsterisk ( (lv_claim_8_0= ruleClaimText ) )+ otherlv_9= AsteriskAsterisk ) ( (lv_context_10_0= ruleClaimContext ) )* ( (lv_assumptions_11_0= ruleClaimAssumption ) )* ( (lv_expr_12_0= ruleExpr ) ) ) ) ;
    public final EObject ruleDefinitionBody() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        EObject lv_type_2_0 = null;

        EObject lv_expr_4_0 = null;

        EObject lv_claim_8_0 = null;

        EObject lv_context_10_0 = null;

        EObject lv_assumptions_11_0 = null;

        EObject lv_expr_12_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1295:28: ( ( ( () otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) ) | ( () otherlv_6= LessThanSignEqualsSign (otherlv_7= AsteriskAsterisk ( (lv_claim_8_0= ruleClaimText ) )+ otherlv_9= AsteriskAsterisk ) ( (lv_context_10_0= ruleClaimContext ) )* ( (lv_assumptions_11_0= ruleClaimAssumption ) )* ( (lv_expr_12_0= ruleExpr ) ) ) ) )
            // InternalResoluteParser.g:1296:1: ( ( () otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) ) | ( () otherlv_6= LessThanSignEqualsSign (otherlv_7= AsteriskAsterisk ( (lv_claim_8_0= ruleClaimText ) )+ otherlv_9= AsteriskAsterisk ) ( (lv_context_10_0= ruleClaimContext ) )* ( (lv_assumptions_11_0= ruleClaimAssumption ) )* ( (lv_expr_12_0= ruleExpr ) ) ) )
            {
            // InternalResoluteParser.g:1296:1: ( ( () otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) ) | ( () otherlv_6= LessThanSignEqualsSign (otherlv_7= AsteriskAsterisk ( (lv_claim_8_0= ruleClaimText ) )+ otherlv_9= AsteriskAsterisk ) ( (lv_context_10_0= ruleClaimContext ) )* ( (lv_assumptions_11_0= ruleClaimAssumption ) )* ( (lv_expr_12_0= ruleExpr ) ) ) )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==Colon) ) {
                alt12=1;
            }
            else if ( (LA12_0==LessThanSignEqualsSign) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // InternalResoluteParser.g:1296:2: ( () otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:1296:2: ( () otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:1296:3: () otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:1296:3: ()
                    // InternalResoluteParser.g:1297:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getDefinitionBodyAccess().getFunctionBodyAction_0_0(),
                                  current);
                          
                    }

                    }

                    otherlv_1=(Token)match(input,Colon,FollowSets000.FOLLOW_4); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getDefinitionBodyAccess().getColonKeyword_0_1());
                          
                    }
                    // InternalResoluteParser.g:1307:1: ( (lv_type_2_0= ruleType ) )
                    // InternalResoluteParser.g:1308:1: (lv_type_2_0= ruleType )
                    {
                    // InternalResoluteParser.g:1308:1: (lv_type_2_0= ruleType )
                    // InternalResoluteParser.g:1309:3: lv_type_2_0= ruleType
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionBodyAccess().getTypeTypeParserRuleCall_0_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_13);
                    lv_type_2_0=ruleType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getDefinitionBodyRule());
                      	        }
                             		set(
                             			current, 
                             			"type",
                              		lv_type_2_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Type");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_3=(Token)match(input,EqualsSign,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_3, grammarAccess.getDefinitionBodyAccess().getEqualsSignKeyword_0_3());
                          
                    }
                    // InternalResoluteParser.g:1330:1: ( (lv_expr_4_0= ruleExpr ) )
                    // InternalResoluteParser.g:1331:1: (lv_expr_4_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:1331:1: (lv_expr_4_0= ruleExpr )
                    // InternalResoluteParser.g:1332:3: lv_expr_4_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionBodyAccess().getExprExprParserRuleCall_0_4_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_4_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getDefinitionBodyRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_4_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:1349:6: ( () otherlv_6= LessThanSignEqualsSign (otherlv_7= AsteriskAsterisk ( (lv_claim_8_0= ruleClaimText ) )+ otherlv_9= AsteriskAsterisk ) ( (lv_context_10_0= ruleClaimContext ) )* ( (lv_assumptions_11_0= ruleClaimAssumption ) )* ( (lv_expr_12_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:1349:6: ( () otherlv_6= LessThanSignEqualsSign (otherlv_7= AsteriskAsterisk ( (lv_claim_8_0= ruleClaimText ) )+ otherlv_9= AsteriskAsterisk ) ( (lv_context_10_0= ruleClaimContext ) )* ( (lv_assumptions_11_0= ruleClaimAssumption ) )* ( (lv_expr_12_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:1349:7: () otherlv_6= LessThanSignEqualsSign (otherlv_7= AsteriskAsterisk ( (lv_claim_8_0= ruleClaimText ) )+ otherlv_9= AsteriskAsterisk ) ( (lv_context_10_0= ruleClaimContext ) )* ( (lv_assumptions_11_0= ruleClaimAssumption ) )* ( (lv_expr_12_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:1349:7: ()
                    // InternalResoluteParser.g:1350:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getDefinitionBodyAccess().getClaimBodyAction_1_0(),
                                  current);
                          
                    }

                    }

                    otherlv_6=(Token)match(input,LessThanSignEqualsSign,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_6, grammarAccess.getDefinitionBodyAccess().getLessThanSignEqualsSignKeyword_1_1());
                          
                    }
                    // InternalResoluteParser.g:1360:1: (otherlv_7= AsteriskAsterisk ( (lv_claim_8_0= ruleClaimText ) )+ otherlv_9= AsteriskAsterisk )
                    // InternalResoluteParser.g:1361:2: otherlv_7= AsteriskAsterisk ( (lv_claim_8_0= ruleClaimText ) )+ otherlv_9= AsteriskAsterisk
                    {
                    otherlv_7=(Token)match(input,AsteriskAsterisk,FollowSets000.FOLLOW_19); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_7, grammarAccess.getDefinitionBodyAccess().getAsteriskAsteriskKeyword_1_2_0());
                          
                    }
                    // InternalResoluteParser.g:1365:1: ( (lv_claim_8_0= ruleClaimText ) )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>=RULE_STRING && LA9_0<=RULE_ID)) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // InternalResoluteParser.g:1366:1: (lv_claim_8_0= ruleClaimText )
                    	    {
                    	    // InternalResoluteParser.g:1366:1: (lv_claim_8_0= ruleClaimText )
                    	    // InternalResoluteParser.g:1367:3: lv_claim_8_0= ruleClaimText
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getDefinitionBodyAccess().getClaimClaimTextParserRuleCall_1_2_1_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_20);
                    	    lv_claim_8_0=ruleClaimText();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getDefinitionBodyRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"claim",
                    	              		lv_claim_8_0, 
                    	              		"com.rockwellcollins.atc.resolute.Resolute.ClaimText");
                    	      	        afterParserOrEnumRuleCall();
                    	      	    
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                    	    if (state.backtracking>0) {state.failed=true; return current;}
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);

                    otherlv_9=(Token)match(input,AsteriskAsterisk,FollowSets000.FOLLOW_21); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_9, grammarAccess.getDefinitionBodyAccess().getAsteriskAsteriskKeyword_1_2_2());
                          
                    }

                    }

                    // InternalResoluteParser.g:1388:2: ( (lv_context_10_0= ruleClaimContext ) )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==Context) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // InternalResoluteParser.g:1389:1: (lv_context_10_0= ruleClaimContext )
                    	    {
                    	    // InternalResoluteParser.g:1389:1: (lv_context_10_0= ruleClaimContext )
                    	    // InternalResoluteParser.g:1390:3: lv_context_10_0= ruleClaimContext
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getDefinitionBodyAccess().getContextClaimContextParserRuleCall_1_3_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_21);
                    	    lv_context_10_0=ruleClaimContext();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getDefinitionBodyRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"context",
                    	              		lv_context_10_0, 
                    	              		"com.rockwellcollins.atc.resolute.Resolute.ClaimContext");
                    	      	        afterParserOrEnumRuleCall();
                    	      	    
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);

                    // InternalResoluteParser.g:1406:3: ( (lv_assumptions_11_0= ruleClaimAssumption ) )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==Assumption) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // InternalResoluteParser.g:1407:1: (lv_assumptions_11_0= ruleClaimAssumption )
                    	    {
                    	    // InternalResoluteParser.g:1407:1: (lv_assumptions_11_0= ruleClaimAssumption )
                    	    // InternalResoluteParser.g:1408:3: lv_assumptions_11_0= ruleClaimAssumption
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getDefinitionBodyAccess().getAssumptionsClaimAssumptionParserRuleCall_1_4_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_22);
                    	    lv_assumptions_11_0=ruleClaimAssumption();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getDefinitionBodyRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"assumptions",
                    	              		lv_assumptions_11_0, 
                    	              		"com.rockwellcollins.atc.resolute.Resolute.ClaimAssumption");
                    	      	        afterParserOrEnumRuleCall();
                    	      	    
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);

                    // InternalResoluteParser.g:1424:3: ( (lv_expr_12_0= ruleExpr ) )
                    // InternalResoluteParser.g:1425:1: (lv_expr_12_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:1425:1: (lv_expr_12_0= ruleExpr )
                    // InternalResoluteParser.g:1426:3: lv_expr_12_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionBodyAccess().getExprExprParserRuleCall_1_5_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_12_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getDefinitionBodyRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_12_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDefinitionBody"


    // $ANTLR start "entryRuleClaimContext"
    // InternalResoluteParser.g:1450:1: entryRuleClaimContext returns [EObject current=null] : iv_ruleClaimContext= ruleClaimContext EOF ;
    public final EObject entryRuleClaimContext() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleClaimContext = null;


        try {
            // InternalResoluteParser.g:1451:2: (iv_ruleClaimContext= ruleClaimContext EOF )
            // InternalResoluteParser.g:1452:2: iv_ruleClaimContext= ruleClaimContext EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getClaimContextRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleClaimContext=ruleClaimContext();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleClaimContext; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleClaimContext"


    // $ANTLR start "ruleClaimContext"
    // InternalResoluteParser.g:1459:1: ruleClaimContext returns [EObject current=null] : (otherlv_0= Context ( (lv_name_1_0= RULE_ID ) ) otherlv_2= Colon ( (lv_val_3_0= ruleStringTerm ) ) ) ;
    public final EObject ruleClaimContext() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        EObject lv_val_3_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1462:28: ( (otherlv_0= Context ( (lv_name_1_0= RULE_ID ) ) otherlv_2= Colon ( (lv_val_3_0= ruleStringTerm ) ) ) )
            // InternalResoluteParser.g:1463:1: (otherlv_0= Context ( (lv_name_1_0= RULE_ID ) ) otherlv_2= Colon ( (lv_val_3_0= ruleStringTerm ) ) )
            {
            // InternalResoluteParser.g:1463:1: (otherlv_0= Context ( (lv_name_1_0= RULE_ID ) ) otherlv_2= Colon ( (lv_val_3_0= ruleStringTerm ) ) )
            // InternalResoluteParser.g:1464:2: otherlv_0= Context ( (lv_name_1_0= RULE_ID ) ) otherlv_2= Colon ( (lv_val_3_0= ruleStringTerm ) )
            {
            otherlv_0=(Token)match(input,Context,FollowSets000.FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getClaimContextAccess().getContextKeyword_0());
                  
            }
            // InternalResoluteParser.g:1468:1: ( (lv_name_1_0= RULE_ID ) )
            // InternalResoluteParser.g:1469:1: (lv_name_1_0= RULE_ID )
            {
            // InternalResoluteParser.g:1469:1: (lv_name_1_0= RULE_ID )
            // InternalResoluteParser.g:1470:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_11); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_1_0, grammarAccess.getClaimContextAccess().getNameIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getClaimContextRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_1_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.ID");
              	    
            }

            }


            }

            otherlv_2=(Token)match(input,Colon,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getClaimContextAccess().getColonKeyword_2());
                  
            }
            // InternalResoluteParser.g:1491:1: ( (lv_val_3_0= ruleStringTerm ) )
            // InternalResoluteParser.g:1492:1: (lv_val_3_0= ruleStringTerm )
            {
            // InternalResoluteParser.g:1492:1: (lv_val_3_0= ruleStringTerm )
            // InternalResoluteParser.g:1493:3: lv_val_3_0= ruleStringTerm
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getClaimContextAccess().getValStringTermParserRuleCall_3_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_val_3_0=ruleStringTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getClaimContextRule());
              	        }
                     		set(
                     			current, 
                     			"val",
                      		lv_val_3_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.StringTerm");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleClaimContext"


    // $ANTLR start "entryRuleClaimAssumption"
    // InternalResoluteParser.g:1517:1: entryRuleClaimAssumption returns [EObject current=null] : iv_ruleClaimAssumption= ruleClaimAssumption EOF ;
    public final EObject entryRuleClaimAssumption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleClaimAssumption = null;


        try {
            // InternalResoluteParser.g:1518:2: (iv_ruleClaimAssumption= ruleClaimAssumption EOF )
            // InternalResoluteParser.g:1519:2: iv_ruleClaimAssumption= ruleClaimAssumption EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getClaimAssumptionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleClaimAssumption=ruleClaimAssumption();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleClaimAssumption; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleClaimAssumption"


    // $ANTLR start "ruleClaimAssumption"
    // InternalResoluteParser.g:1526:1: ruleClaimAssumption returns [EObject current=null] : (otherlv_0= Assumption ( (lv_name_1_0= RULE_ID ) ) otherlv_2= Colon ( (lv_val_3_0= ruleStringTerm ) ) ) ;
    public final EObject ruleClaimAssumption() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        EObject lv_val_3_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1529:28: ( (otherlv_0= Assumption ( (lv_name_1_0= RULE_ID ) ) otherlv_2= Colon ( (lv_val_3_0= ruleStringTerm ) ) ) )
            // InternalResoluteParser.g:1530:1: (otherlv_0= Assumption ( (lv_name_1_0= RULE_ID ) ) otherlv_2= Colon ( (lv_val_3_0= ruleStringTerm ) ) )
            {
            // InternalResoluteParser.g:1530:1: (otherlv_0= Assumption ( (lv_name_1_0= RULE_ID ) ) otherlv_2= Colon ( (lv_val_3_0= ruleStringTerm ) ) )
            // InternalResoluteParser.g:1531:2: otherlv_0= Assumption ( (lv_name_1_0= RULE_ID ) ) otherlv_2= Colon ( (lv_val_3_0= ruleStringTerm ) )
            {
            otherlv_0=(Token)match(input,Assumption,FollowSets000.FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getClaimAssumptionAccess().getAssumptionKeyword_0());
                  
            }
            // InternalResoluteParser.g:1535:1: ( (lv_name_1_0= RULE_ID ) )
            // InternalResoluteParser.g:1536:1: (lv_name_1_0= RULE_ID )
            {
            // InternalResoluteParser.g:1536:1: (lv_name_1_0= RULE_ID )
            // InternalResoluteParser.g:1537:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_11); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_1_0, grammarAccess.getClaimAssumptionAccess().getNameIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getClaimAssumptionRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_1_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.ID");
              	    
            }

            }


            }

            otherlv_2=(Token)match(input,Colon,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getClaimAssumptionAccess().getColonKeyword_2());
                  
            }
            // InternalResoluteParser.g:1558:1: ( (lv_val_3_0= ruleStringTerm ) )
            // InternalResoluteParser.g:1559:1: (lv_val_3_0= ruleStringTerm )
            {
            // InternalResoluteParser.g:1559:1: (lv_val_3_0= ruleStringTerm )
            // InternalResoluteParser.g:1560:3: lv_val_3_0= ruleStringTerm
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getClaimAssumptionAccess().getValStringTermParserRuleCall_3_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_val_3_0=ruleStringTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getClaimAssumptionRule());
              	        }
                     		set(
                     			current, 
                     			"val",
                      		lv_val_3_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.StringTerm");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleClaimAssumption"


    // $ANTLR start "entryRuleClaimText"
    // InternalResoluteParser.g:1584:1: entryRuleClaimText returns [EObject current=null] : iv_ruleClaimText= ruleClaimText EOF ;
    public final EObject entryRuleClaimText() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleClaimText = null;


        try {
            // InternalResoluteParser.g:1585:2: (iv_ruleClaimText= ruleClaimText EOF )
            // InternalResoluteParser.g:1586:2: iv_ruleClaimText= ruleClaimText EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getClaimTextRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleClaimText=ruleClaimText();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleClaimText; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleClaimText"


    // $ANTLR start "ruleClaimText"
    // InternalResoluteParser.g:1593:1: ruleClaimText returns [EObject current=null] : ( ( () ( (lv_str_1_0= RULE_STRING ) ) ) | ( () ( (otherlv_3= RULE_ID ) ) (otherlv_4= PercentSign ( (otherlv_5= RULE_ID ) ) )? ) ) ;
    public final EObject ruleClaimText() throws RecognitionException {
        EObject current = null;

        Token lv_str_1_0=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1596:28: ( ( ( () ( (lv_str_1_0= RULE_STRING ) ) ) | ( () ( (otherlv_3= RULE_ID ) ) (otherlv_4= PercentSign ( (otherlv_5= RULE_ID ) ) )? ) ) )
            // InternalResoluteParser.g:1597:1: ( ( () ( (lv_str_1_0= RULE_STRING ) ) ) | ( () ( (otherlv_3= RULE_ID ) ) (otherlv_4= PercentSign ( (otherlv_5= RULE_ID ) ) )? ) )
            {
            // InternalResoluteParser.g:1597:1: ( ( () ( (lv_str_1_0= RULE_STRING ) ) ) | ( () ( (otherlv_3= RULE_ID ) ) (otherlv_4= PercentSign ( (otherlv_5= RULE_ID ) ) )? ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_STRING) ) {
                alt14=1;
            }
            else if ( (LA14_0==RULE_ID) ) {
                alt14=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // InternalResoluteParser.g:1597:2: ( () ( (lv_str_1_0= RULE_STRING ) ) )
                    {
                    // InternalResoluteParser.g:1597:2: ( () ( (lv_str_1_0= RULE_STRING ) ) )
                    // InternalResoluteParser.g:1597:3: () ( (lv_str_1_0= RULE_STRING ) )
                    {
                    // InternalResoluteParser.g:1597:3: ()
                    // InternalResoluteParser.g:1598:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getClaimTextAccess().getClaimStringAction_0_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:1603:2: ( (lv_str_1_0= RULE_STRING ) )
                    // InternalResoluteParser.g:1604:1: (lv_str_1_0= RULE_STRING )
                    {
                    // InternalResoluteParser.g:1604:1: (lv_str_1_0= RULE_STRING )
                    // InternalResoluteParser.g:1605:3: lv_str_1_0= RULE_STRING
                    {
                    lv_str_1_0=(Token)match(input,RULE_STRING,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_str_1_0, grammarAccess.getClaimTextAccess().getStrSTRINGTerminalRuleCall_0_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getClaimTextRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"str",
                              		lv_str_1_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.STRING");
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:1622:6: ( () ( (otherlv_3= RULE_ID ) ) (otherlv_4= PercentSign ( (otherlv_5= RULE_ID ) ) )? )
                    {
                    // InternalResoluteParser.g:1622:6: ( () ( (otherlv_3= RULE_ID ) ) (otherlv_4= PercentSign ( (otherlv_5= RULE_ID ) ) )? )
                    // InternalResoluteParser.g:1622:7: () ( (otherlv_3= RULE_ID ) ) (otherlv_4= PercentSign ( (otherlv_5= RULE_ID ) ) )?
                    {
                    // InternalResoluteParser.g:1622:7: ()
                    // InternalResoluteParser.g:1623:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getClaimTextAccess().getClaimArgAction_1_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:1628:2: ( (otherlv_3= RULE_ID ) )
                    // InternalResoluteParser.g:1629:1: (otherlv_3= RULE_ID )
                    {
                    // InternalResoluteParser.g:1629:1: (otherlv_3= RULE_ID )
                    // InternalResoluteParser.g:1630:3: otherlv_3= RULE_ID
                    {
                    if ( state.backtracking==0 ) {

                      			if (current==null) {
                      	            current = createModelElement(grammarAccess.getClaimTextRule());
                      	        }
                              
                    }
                    otherlv_3=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_24); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		newLeafNode(otherlv_3, grammarAccess.getClaimTextAccess().getArgClaimTextVarCrossReference_1_1_0()); 
                      	
                    }

                    }


                    }

                    // InternalResoluteParser.g:1641:2: (otherlv_4= PercentSign ( (otherlv_5= RULE_ID ) ) )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==PercentSign) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // InternalResoluteParser.g:1642:2: otherlv_4= PercentSign ( (otherlv_5= RULE_ID ) )
                            {
                            otherlv_4=(Token)match(input,PercentSign,FollowSets000.FOLLOW_10); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                  	newLeafNode(otherlv_4, grammarAccess.getClaimTextAccess().getPercentSignKeyword_1_2_0());
                                  
                            }
                            // InternalResoluteParser.g:1646:1: ( (otherlv_5= RULE_ID ) )
                            // InternalResoluteParser.g:1647:1: (otherlv_5= RULE_ID )
                            {
                            // InternalResoluteParser.g:1647:1: (otherlv_5= RULE_ID )
                            // InternalResoluteParser.g:1648:3: otherlv_5= RULE_ID
                            {
                            if ( state.backtracking==0 ) {

                              			if (current==null) {
                              	            current = createModelElement(grammarAccess.getClaimTextRule());
                              	        }
                                      
                            }
                            otherlv_5=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_2); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              		newLeafNode(otherlv_5, grammarAccess.getClaimTextAccess().getUnitUnitLiteralCrossReference_1_2_1_0()); 
                              	
                            }

                            }


                            }


                            }
                            break;

                    }


                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleClaimText"


    // $ANTLR start "entryRuleExpr"
    // InternalResoluteParser.g:1669:1: entryRuleExpr returns [EObject current=null] : iv_ruleExpr= ruleExpr EOF ;
    public final EObject entryRuleExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpr = null;


        try {
            // InternalResoluteParser.g:1670:2: (iv_ruleExpr= ruleExpr EOF )
            // InternalResoluteParser.g:1671:2: iv_ruleExpr= ruleExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleExpr=ruleExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleExpr"


    // $ANTLR start "ruleExpr"
    // InternalResoluteParser.g:1678:1: ruleExpr returns [EObject current=null] : this_ImpliesExpr_0= ruleImpliesExpr ;
    public final EObject ruleExpr() throws RecognitionException {
        EObject current = null;

        EObject this_ImpliesExpr_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1681:28: (this_ImpliesExpr_0= ruleImpliesExpr )
            // InternalResoluteParser.g:1683:5: this_ImpliesExpr_0= ruleImpliesExpr
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getExprAccess().getImpliesExprParserRuleCall()); 
                  
            }
            pushFollow(FollowSets000.FOLLOW_2);
            this_ImpliesExpr_0=ruleImpliesExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current = this_ImpliesExpr_0;
                      afterParserOrEnumRuleCall();
                  
            }

            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleExpr"


    // $ANTLR start "entryRuleImpliesExpr"
    // InternalResoluteParser.g:1699:1: entryRuleImpliesExpr returns [EObject current=null] : iv_ruleImpliesExpr= ruleImpliesExpr EOF ;
    public final EObject entryRuleImpliesExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImpliesExpr = null;


        try {
            // InternalResoluteParser.g:1700:2: (iv_ruleImpliesExpr= ruleImpliesExpr EOF )
            // InternalResoluteParser.g:1701:2: iv_ruleImpliesExpr= ruleImpliesExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getImpliesExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleImpliesExpr=ruleImpliesExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleImpliesExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleImpliesExpr"


    // $ANTLR start "ruleImpliesExpr"
    // InternalResoluteParser.g:1708:1: ruleImpliesExpr returns [EObject current=null] : (this_OrExpr_0= ruleOrExpr ( ( ( ( () ( ( EqualsSignGreaterThanSign ) ) ) )=> ( () ( (lv_op_2_0= EqualsSignGreaterThanSign ) ) ) ) ( (lv_right_3_0= ruleImpliesExpr ) ) )? ) ;
    public final EObject ruleImpliesExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_OrExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1711:28: ( (this_OrExpr_0= ruleOrExpr ( ( ( ( () ( ( EqualsSignGreaterThanSign ) ) ) )=> ( () ( (lv_op_2_0= EqualsSignGreaterThanSign ) ) ) ) ( (lv_right_3_0= ruleImpliesExpr ) ) )? ) )
            // InternalResoluteParser.g:1712:1: (this_OrExpr_0= ruleOrExpr ( ( ( ( () ( ( EqualsSignGreaterThanSign ) ) ) )=> ( () ( (lv_op_2_0= EqualsSignGreaterThanSign ) ) ) ) ( (lv_right_3_0= ruleImpliesExpr ) ) )? )
            {
            // InternalResoluteParser.g:1712:1: (this_OrExpr_0= ruleOrExpr ( ( ( ( () ( ( EqualsSignGreaterThanSign ) ) ) )=> ( () ( (lv_op_2_0= EqualsSignGreaterThanSign ) ) ) ) ( (lv_right_3_0= ruleImpliesExpr ) ) )? )
            // InternalResoluteParser.g:1713:5: this_OrExpr_0= ruleOrExpr ( ( ( ( () ( ( EqualsSignGreaterThanSign ) ) ) )=> ( () ( (lv_op_2_0= EqualsSignGreaterThanSign ) ) ) ) ( (lv_right_3_0= ruleImpliesExpr ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getImpliesExprAccess().getOrExprParserRuleCall_0()); 
                  
            }
            pushFollow(FollowSets000.FOLLOW_25);
            this_OrExpr_0=ruleOrExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current = this_OrExpr_0;
                      afterParserOrEnumRuleCall();
                  
            }
            // InternalResoluteParser.g:1721:1: ( ( ( ( () ( ( EqualsSignGreaterThanSign ) ) ) )=> ( () ( (lv_op_2_0= EqualsSignGreaterThanSign ) ) ) ) ( (lv_right_3_0= ruleImpliesExpr ) ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==EqualsSignGreaterThanSign) ) {
                int LA15_1 = input.LA(2);

                if ( (synpred2_InternalResoluteParser()) ) {
                    alt15=1;
                }
            }
            switch (alt15) {
                case 1 :
                    // InternalResoluteParser.g:1721:2: ( ( ( () ( ( EqualsSignGreaterThanSign ) ) ) )=> ( () ( (lv_op_2_0= EqualsSignGreaterThanSign ) ) ) ) ( (lv_right_3_0= ruleImpliesExpr ) )
                    {
                    // InternalResoluteParser.g:1721:2: ( ( ( () ( ( EqualsSignGreaterThanSign ) ) ) )=> ( () ( (lv_op_2_0= EqualsSignGreaterThanSign ) ) ) )
                    // InternalResoluteParser.g:1721:3: ( ( () ( ( EqualsSignGreaterThanSign ) ) ) )=> ( () ( (lv_op_2_0= EqualsSignGreaterThanSign ) ) )
                    {
                    // InternalResoluteParser.g:1729:6: ( () ( (lv_op_2_0= EqualsSignGreaterThanSign ) ) )
                    // InternalResoluteParser.g:1729:7: () ( (lv_op_2_0= EqualsSignGreaterThanSign ) )
                    {
                    // InternalResoluteParser.g:1729:7: ()
                    // InternalResoluteParser.g:1730:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getImpliesExprAccess().getBinaryExprLeftAction_1_0_0_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:1735:2: ( (lv_op_2_0= EqualsSignGreaterThanSign ) )
                    // InternalResoluteParser.g:1736:1: (lv_op_2_0= EqualsSignGreaterThanSign )
                    {
                    // InternalResoluteParser.g:1736:1: (lv_op_2_0= EqualsSignGreaterThanSign )
                    // InternalResoluteParser.g:1737:3: lv_op_2_0= EqualsSignGreaterThanSign
                    {
                    lv_op_2_0=(Token)match(input,EqualsSignGreaterThanSign,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_op_2_0, grammarAccess.getImpliesExprAccess().getOpEqualsSignGreaterThanSignKeyword_1_0_0_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getImpliesExprRule());
                      	        }
                             		setWithLastConsumed(current, "op", lv_op_2_0, "=>");
                      	    
                    }

                    }


                    }


                    }


                    }

                    // InternalResoluteParser.g:1751:4: ( (lv_right_3_0= ruleImpliesExpr ) )
                    // InternalResoluteParser.g:1752:1: (lv_right_3_0= ruleImpliesExpr )
                    {
                    // InternalResoluteParser.g:1752:1: (lv_right_3_0= ruleImpliesExpr )
                    // InternalResoluteParser.g:1753:3: lv_right_3_0= ruleImpliesExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getImpliesExprAccess().getRightImpliesExprParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_right_3_0=ruleImpliesExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getImpliesExprRule());
                      	        }
                             		set(
                             			current, 
                             			"right",
                              		lv_right_3_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.ImpliesExpr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleImpliesExpr"


    // $ANTLR start "entryRuleOrExpr"
    // InternalResoluteParser.g:1777:1: entryRuleOrExpr returns [EObject current=null] : iv_ruleOrExpr= ruleOrExpr EOF ;
    public final EObject entryRuleOrExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrExpr = null;


        try {
            // InternalResoluteParser.g:1778:2: (iv_ruleOrExpr= ruleOrExpr EOF )
            // InternalResoluteParser.g:1779:2: iv_ruleOrExpr= ruleOrExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOrExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleOrExpr=ruleOrExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOrExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOrExpr"


    // $ANTLR start "ruleOrExpr"
    // InternalResoluteParser.g:1786:1: ruleOrExpr returns [EObject current=null] : (this_AndExpr_0= ruleAndExpr ( ( ( ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) ) )=> ( () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) ) ) ) ( (lv_right_4_0= ruleAndExpr ) ) )* ) ;
    public final EObject ruleOrExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        Token lv_op_3_0=null;
        EObject this_AndExpr_0 = null;

        EObject lv_right_4_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1789:28: ( (this_AndExpr_0= ruleAndExpr ( ( ( ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) ) )=> ( () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) ) ) ) ( (lv_right_4_0= ruleAndExpr ) ) )* ) )
            // InternalResoluteParser.g:1790:1: (this_AndExpr_0= ruleAndExpr ( ( ( ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) ) )=> ( () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) ) ) ) ( (lv_right_4_0= ruleAndExpr ) ) )* )
            {
            // InternalResoluteParser.g:1790:1: (this_AndExpr_0= ruleAndExpr ( ( ( ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) ) )=> ( () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) ) ) ) ( (lv_right_4_0= ruleAndExpr ) ) )* )
            // InternalResoluteParser.g:1791:5: this_AndExpr_0= ruleAndExpr ( ( ( ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) ) )=> ( () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) ) ) ) ( (lv_right_4_0= ruleAndExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getOrExprAccess().getAndExprParserRuleCall_0()); 
                  
            }
            pushFollow(FollowSets000.FOLLOW_26);
            this_AndExpr_0=ruleAndExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current = this_AndExpr_0;
                      afterParserOrEnumRuleCall();
                  
            }
            // InternalResoluteParser.g:1799:1: ( ( ( ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) ) )=> ( () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) ) ) ) ( (lv_right_4_0= ruleAndExpr ) ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==Or) ) {
                    int LA17_2 = input.LA(2);

                    if ( (synpred3_InternalResoluteParser()) ) {
                        alt17=1;
                    }


                }
                else if ( (LA17_0==Orelse) ) {
                    int LA17_3 = input.LA(2);

                    if ( (synpred3_InternalResoluteParser()) ) {
                        alt17=1;
                    }


                }


                switch (alt17) {
            	case 1 :
            	    // InternalResoluteParser.g:1799:2: ( ( ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) ) )=> ( () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) ) ) ) ( (lv_right_4_0= ruleAndExpr ) )
            	    {
            	    // InternalResoluteParser.g:1799:2: ( ( ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) ) )=> ( () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) ) ) )
            	    // InternalResoluteParser.g:1799:3: ( ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) ) )=> ( () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) ) )
            	    {
            	    // InternalResoluteParser.g:1815:7: ( () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) ) )
            	    // InternalResoluteParser.g:1815:8: () ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) )
            	    {
            	    // InternalResoluteParser.g:1815:8: ()
            	    // InternalResoluteParser.g:1816:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getOrExprAccess().getBinaryExprLeftAction_1_0_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // InternalResoluteParser.g:1821:2: ( ( (lv_op_2_0= Or ) ) | ( (lv_op_3_0= Orelse ) ) )
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);

            	    if ( (LA16_0==Or) ) {
            	        alt16=1;
            	    }
            	    else if ( (LA16_0==Orelse) ) {
            	        alt16=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 16, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // InternalResoluteParser.g:1821:3: ( (lv_op_2_0= Or ) )
            	            {
            	            // InternalResoluteParser.g:1821:3: ( (lv_op_2_0= Or ) )
            	            // InternalResoluteParser.g:1822:1: (lv_op_2_0= Or )
            	            {
            	            // InternalResoluteParser.g:1822:1: (lv_op_2_0= Or )
            	            // InternalResoluteParser.g:1823:3: lv_op_2_0= Or
            	            {
            	            lv_op_2_0=(Token)match(input,Or,FollowSets000.FOLLOW_12); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_0, grammarAccess.getOrExprAccess().getOpOrKeyword_1_0_0_1_0_0());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getOrExprRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_0, "or");
            	              	    
            	            }

            	            }


            	            }


            	            }
            	            break;
            	        case 2 :
            	            // InternalResoluteParser.g:1838:6: ( (lv_op_3_0= Orelse ) )
            	            {
            	            // InternalResoluteParser.g:1838:6: ( (lv_op_3_0= Orelse ) )
            	            // InternalResoluteParser.g:1839:1: (lv_op_3_0= Orelse )
            	            {
            	            // InternalResoluteParser.g:1839:1: (lv_op_3_0= Orelse )
            	            // InternalResoluteParser.g:1840:3: lv_op_3_0= Orelse
            	            {
            	            lv_op_3_0=(Token)match(input,Orelse,FollowSets000.FOLLOW_12); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_3_0, grammarAccess.getOrExprAccess().getOpOrelseKeyword_1_0_0_1_1_0());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getOrExprRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_3_0, "orelse");
            	              	    
            	            }

            	            }


            	            }


            	            }
            	            break;

            	    }


            	    }


            	    }

            	    // InternalResoluteParser.g:1854:5: ( (lv_right_4_0= ruleAndExpr ) )
            	    // InternalResoluteParser.g:1855:1: (lv_right_4_0= ruleAndExpr )
            	    {
            	    // InternalResoluteParser.g:1855:1: (lv_right_4_0= ruleAndExpr )
            	    // InternalResoluteParser.g:1856:3: lv_right_4_0= ruleAndExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getOrExprAccess().getRightAndExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_26);
            	    lv_right_4_0=ruleAndExpr();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getOrExprRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_4_0, 
            	              		"com.rockwellcollins.atc.resolute.Resolute.AndExpr");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOrExpr"


    // $ANTLR start "entryRuleAndExpr"
    // InternalResoluteParser.g:1880:1: entryRuleAndExpr returns [EObject current=null] : iv_ruleAndExpr= ruleAndExpr EOF ;
    public final EObject entryRuleAndExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndExpr = null;


        try {
            // InternalResoluteParser.g:1881:2: (iv_ruleAndExpr= ruleAndExpr EOF )
            // InternalResoluteParser.g:1882:2: iv_ruleAndExpr= ruleAndExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAndExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAndExpr=ruleAndExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAndExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAndExpr"


    // $ANTLR start "ruleAndExpr"
    // InternalResoluteParser.g:1889:1: ruleAndExpr returns [EObject current=null] : (this_InstanceOfExpr_0= ruleInstanceOfExpr ( ( ( ( () ( ( ( And ) ) | ( ( Andthen ) ) ) ) )=> ( () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) ) ) ) ( (lv_right_4_0= ruleInstanceOfExpr ) ) )* ) ;
    public final EObject ruleAndExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        Token lv_op_3_0=null;
        EObject this_InstanceOfExpr_0 = null;

        EObject lv_right_4_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1892:28: ( (this_InstanceOfExpr_0= ruleInstanceOfExpr ( ( ( ( () ( ( ( And ) ) | ( ( Andthen ) ) ) ) )=> ( () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) ) ) ) ( (lv_right_4_0= ruleInstanceOfExpr ) ) )* ) )
            // InternalResoluteParser.g:1893:1: (this_InstanceOfExpr_0= ruleInstanceOfExpr ( ( ( ( () ( ( ( And ) ) | ( ( Andthen ) ) ) ) )=> ( () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) ) ) ) ( (lv_right_4_0= ruleInstanceOfExpr ) ) )* )
            {
            // InternalResoluteParser.g:1893:1: (this_InstanceOfExpr_0= ruleInstanceOfExpr ( ( ( ( () ( ( ( And ) ) | ( ( Andthen ) ) ) ) )=> ( () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) ) ) ) ( (lv_right_4_0= ruleInstanceOfExpr ) ) )* )
            // InternalResoluteParser.g:1894:5: this_InstanceOfExpr_0= ruleInstanceOfExpr ( ( ( ( () ( ( ( And ) ) | ( ( Andthen ) ) ) ) )=> ( () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) ) ) ) ( (lv_right_4_0= ruleInstanceOfExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAndExprAccess().getInstanceOfExprParserRuleCall_0()); 
                  
            }
            pushFollow(FollowSets000.FOLLOW_27);
            this_InstanceOfExpr_0=ruleInstanceOfExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current = this_InstanceOfExpr_0;
                      afterParserOrEnumRuleCall();
                  
            }
            // InternalResoluteParser.g:1902:1: ( ( ( ( () ( ( ( And ) ) | ( ( Andthen ) ) ) ) )=> ( () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) ) ) ) ( (lv_right_4_0= ruleInstanceOfExpr ) ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==And) ) {
                    int LA19_2 = input.LA(2);

                    if ( (synpred4_InternalResoluteParser()) ) {
                        alt19=1;
                    }


                }
                else if ( (LA19_0==Andthen) ) {
                    int LA19_3 = input.LA(2);

                    if ( (synpred4_InternalResoluteParser()) ) {
                        alt19=1;
                    }


                }


                switch (alt19) {
            	case 1 :
            	    // InternalResoluteParser.g:1902:2: ( ( ( () ( ( ( And ) ) | ( ( Andthen ) ) ) ) )=> ( () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) ) ) ) ( (lv_right_4_0= ruleInstanceOfExpr ) )
            	    {
            	    // InternalResoluteParser.g:1902:2: ( ( ( () ( ( ( And ) ) | ( ( Andthen ) ) ) ) )=> ( () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) ) ) )
            	    // InternalResoluteParser.g:1902:3: ( ( () ( ( ( And ) ) | ( ( Andthen ) ) ) ) )=> ( () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) ) )
            	    {
            	    // InternalResoluteParser.g:1918:7: ( () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) ) )
            	    // InternalResoluteParser.g:1918:8: () ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) )
            	    {
            	    // InternalResoluteParser.g:1918:8: ()
            	    // InternalResoluteParser.g:1919:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getAndExprAccess().getBinaryExprLeftAction_1_0_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // InternalResoluteParser.g:1924:2: ( ( (lv_op_2_0= And ) ) | ( (lv_op_3_0= Andthen ) ) )
            	    int alt18=2;
            	    int LA18_0 = input.LA(1);

            	    if ( (LA18_0==And) ) {
            	        alt18=1;
            	    }
            	    else if ( (LA18_0==Andthen) ) {
            	        alt18=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 18, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt18) {
            	        case 1 :
            	            // InternalResoluteParser.g:1924:3: ( (lv_op_2_0= And ) )
            	            {
            	            // InternalResoluteParser.g:1924:3: ( (lv_op_2_0= And ) )
            	            // InternalResoluteParser.g:1925:1: (lv_op_2_0= And )
            	            {
            	            // InternalResoluteParser.g:1925:1: (lv_op_2_0= And )
            	            // InternalResoluteParser.g:1926:3: lv_op_2_0= And
            	            {
            	            lv_op_2_0=(Token)match(input,And,FollowSets000.FOLLOW_12); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_0, grammarAccess.getAndExprAccess().getOpAndKeyword_1_0_0_1_0_0());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getAndExprRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_0, "and");
            	              	    
            	            }

            	            }


            	            }


            	            }
            	            break;
            	        case 2 :
            	            // InternalResoluteParser.g:1941:6: ( (lv_op_3_0= Andthen ) )
            	            {
            	            // InternalResoluteParser.g:1941:6: ( (lv_op_3_0= Andthen ) )
            	            // InternalResoluteParser.g:1942:1: (lv_op_3_0= Andthen )
            	            {
            	            // InternalResoluteParser.g:1942:1: (lv_op_3_0= Andthen )
            	            // InternalResoluteParser.g:1943:3: lv_op_3_0= Andthen
            	            {
            	            lv_op_3_0=(Token)match(input,Andthen,FollowSets000.FOLLOW_12); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_3_0, grammarAccess.getAndExprAccess().getOpAndthenKeyword_1_0_0_1_1_0());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getAndExprRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_3_0, "andthen");
            	              	    
            	            }

            	            }


            	            }


            	            }
            	            break;

            	    }


            	    }


            	    }

            	    // InternalResoluteParser.g:1957:5: ( (lv_right_4_0= ruleInstanceOfExpr ) )
            	    // InternalResoluteParser.g:1958:1: (lv_right_4_0= ruleInstanceOfExpr )
            	    {
            	    // InternalResoluteParser.g:1958:1: (lv_right_4_0= ruleInstanceOfExpr )
            	    // InternalResoluteParser.g:1959:3: lv_right_4_0= ruleInstanceOfExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAndExprAccess().getRightInstanceOfExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_27);
            	    lv_right_4_0=ruleInstanceOfExpr();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getAndExprRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_4_0, 
            	              		"com.rockwellcollins.atc.resolute.Resolute.InstanceOfExpr");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAndExpr"


    // $ANTLR start "entryRuleInstanceOfExpr"
    // InternalResoluteParser.g:1983:1: entryRuleInstanceOfExpr returns [EObject current=null] : iv_ruleInstanceOfExpr= ruleInstanceOfExpr EOF ;
    public final EObject entryRuleInstanceOfExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleInstanceOfExpr = null;


        try {
            // InternalResoluteParser.g:1984:2: (iv_ruleInstanceOfExpr= ruleInstanceOfExpr EOF )
            // InternalResoluteParser.g:1985:2: iv_ruleInstanceOfExpr= ruleInstanceOfExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getInstanceOfExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleInstanceOfExpr=ruleInstanceOfExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleInstanceOfExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleInstanceOfExpr"


    // $ANTLR start "ruleInstanceOfExpr"
    // InternalResoluteParser.g:1992:1: ruleInstanceOfExpr returns [EObject current=null] : (this_RelationalExpr_0= ruleRelationalExpr ( ( ( ( () Instanceof ) )=> ( () otherlv_2= Instanceof ) ) ( (lv_type_3_0= ruleBaseType ) ) )? ) ;
    public final EObject ruleInstanceOfExpr() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_RelationalExpr_0 = null;

        EObject lv_type_3_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:1995:28: ( (this_RelationalExpr_0= ruleRelationalExpr ( ( ( ( () Instanceof ) )=> ( () otherlv_2= Instanceof ) ) ( (lv_type_3_0= ruleBaseType ) ) )? ) )
            // InternalResoluteParser.g:1996:1: (this_RelationalExpr_0= ruleRelationalExpr ( ( ( ( () Instanceof ) )=> ( () otherlv_2= Instanceof ) ) ( (lv_type_3_0= ruleBaseType ) ) )? )
            {
            // InternalResoluteParser.g:1996:1: (this_RelationalExpr_0= ruleRelationalExpr ( ( ( ( () Instanceof ) )=> ( () otherlv_2= Instanceof ) ) ( (lv_type_3_0= ruleBaseType ) ) )? )
            // InternalResoluteParser.g:1997:5: this_RelationalExpr_0= ruleRelationalExpr ( ( ( ( () Instanceof ) )=> ( () otherlv_2= Instanceof ) ) ( (lv_type_3_0= ruleBaseType ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getInstanceOfExprAccess().getRelationalExprParserRuleCall_0()); 
                  
            }
            pushFollow(FollowSets000.FOLLOW_28);
            this_RelationalExpr_0=ruleRelationalExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current = this_RelationalExpr_0;
                      afterParserOrEnumRuleCall();
                  
            }
            // InternalResoluteParser.g:2005:1: ( ( ( ( () Instanceof ) )=> ( () otherlv_2= Instanceof ) ) ( (lv_type_3_0= ruleBaseType ) ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==Instanceof) ) {
                int LA20_1 = input.LA(2);

                if ( (synpred5_InternalResoluteParser()) ) {
                    alt20=1;
                }
            }
            switch (alt20) {
                case 1 :
                    // InternalResoluteParser.g:2005:2: ( ( ( () Instanceof ) )=> ( () otherlv_2= Instanceof ) ) ( (lv_type_3_0= ruleBaseType ) )
                    {
                    // InternalResoluteParser.g:2005:2: ( ( ( () Instanceof ) )=> ( () otherlv_2= Instanceof ) )
                    // InternalResoluteParser.g:2005:3: ( ( () Instanceof ) )=> ( () otherlv_2= Instanceof )
                    {
                    // InternalResoluteParser.g:2008:5: ( () otherlv_2= Instanceof )
                    // InternalResoluteParser.g:2008:6: () otherlv_2= Instanceof
                    {
                    // InternalResoluteParser.g:2008:6: ()
                    // InternalResoluteParser.g:2009:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getInstanceOfExprAccess().getInstanceOfExprExprAction_1_0_0_0(),
                                  current);
                          
                    }

                    }

                    otherlv_2=(Token)match(input,Instanceof,FollowSets000.FOLLOW_29); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_2, grammarAccess.getInstanceOfExprAccess().getInstanceofKeyword_1_0_0_1());
                          
                    }

                    }


                    }

                    // InternalResoluteParser.g:2019:3: ( (lv_type_3_0= ruleBaseType ) )
                    // InternalResoluteParser.g:2020:1: (lv_type_3_0= ruleBaseType )
                    {
                    // InternalResoluteParser.g:2020:1: (lv_type_3_0= ruleBaseType )
                    // InternalResoluteParser.g:2021:3: lv_type_3_0= ruleBaseType
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getInstanceOfExprAccess().getTypeBaseTypeParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_type_3_0=ruleBaseType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getInstanceOfExprRule());
                      	        }
                             		set(
                             			current, 
                             			"type",
                              		lv_type_3_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.BaseType");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleInstanceOfExpr"


    // $ANTLR start "entryRuleRelationalOp"
    // InternalResoluteParser.g:2045:1: entryRuleRelationalOp returns [String current=null] : iv_ruleRelationalOp= ruleRelationalOp EOF ;
    public final String entryRuleRelationalOp() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRelationalOp = null;


        try {
            // InternalResoluteParser.g:2046:1: (iv_ruleRelationalOp= ruleRelationalOp EOF )
            // InternalResoluteParser.g:2047:2: iv_ruleRelationalOp= ruleRelationalOp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRelationalOpRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleRelationalOp=ruleRelationalOp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRelationalOp.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRelationalOp"


    // $ANTLR start "ruleRelationalOp"
    // InternalResoluteParser.g:2054:1: ruleRelationalOp returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= LessThanSign | kw= LessThanSignEqualsSign | kw= GreaterThanSign | kw= GreaterThanSignEqualsSign | kw= EqualsSign | kw= LessThanSignGreaterThanSign ) ;
    public final AntlrDatatypeRuleToken ruleRelationalOp() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:2058:6: ( (kw= LessThanSign | kw= LessThanSignEqualsSign | kw= GreaterThanSign | kw= GreaterThanSignEqualsSign | kw= EqualsSign | kw= LessThanSignGreaterThanSign ) )
            // InternalResoluteParser.g:2059:1: (kw= LessThanSign | kw= LessThanSignEqualsSign | kw= GreaterThanSign | kw= GreaterThanSignEqualsSign | kw= EqualsSign | kw= LessThanSignGreaterThanSign )
            {
            // InternalResoluteParser.g:2059:1: (kw= LessThanSign | kw= LessThanSignEqualsSign | kw= GreaterThanSign | kw= GreaterThanSignEqualsSign | kw= EqualsSign | kw= LessThanSignGreaterThanSign )
            int alt21=6;
            switch ( input.LA(1) ) {
            case LessThanSign:
                {
                alt21=1;
                }
                break;
            case LessThanSignEqualsSign:
                {
                alt21=2;
                }
                break;
            case GreaterThanSign:
                {
                alt21=3;
                }
                break;
            case GreaterThanSignEqualsSign:
                {
                alt21=4;
                }
                break;
            case EqualsSign:
                {
                alt21=5;
                }
                break;
            case LessThanSignGreaterThanSign:
                {
                alt21=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // InternalResoluteParser.g:2060:2: kw= LessThanSign
                    {
                    kw=(Token)match(input,LessThanSign,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getRelationalOpAccess().getLessThanSignKeyword_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:2067:2: kw= LessThanSignEqualsSign
                    {
                    kw=(Token)match(input,LessThanSignEqualsSign,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getRelationalOpAccess().getLessThanSignEqualsSignKeyword_1()); 
                          
                    }

                    }
                    break;
                case 3 :
                    // InternalResoluteParser.g:2074:2: kw= GreaterThanSign
                    {
                    kw=(Token)match(input,GreaterThanSign,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getRelationalOpAccess().getGreaterThanSignKeyword_2()); 
                          
                    }

                    }
                    break;
                case 4 :
                    // InternalResoluteParser.g:2081:2: kw= GreaterThanSignEqualsSign
                    {
                    kw=(Token)match(input,GreaterThanSignEqualsSign,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getRelationalOpAccess().getGreaterThanSignEqualsSignKeyword_3()); 
                          
                    }

                    }
                    break;
                case 5 :
                    // InternalResoluteParser.g:2088:2: kw= EqualsSign
                    {
                    kw=(Token)match(input,EqualsSign,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getRelationalOpAccess().getEqualsSignKeyword_4()); 
                          
                    }

                    }
                    break;
                case 6 :
                    // InternalResoluteParser.g:2095:2: kw= LessThanSignGreaterThanSign
                    {
                    kw=(Token)match(input,LessThanSignGreaterThanSign,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getRelationalOpAccess().getLessThanSignGreaterThanSignKeyword_5()); 
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationalOp"


    // $ANTLR start "entryRuleRelationalExpr"
    // InternalResoluteParser.g:2108:1: entryRuleRelationalExpr returns [EObject current=null] : iv_ruleRelationalExpr= ruleRelationalExpr EOF ;
    public final EObject entryRuleRelationalExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationalExpr = null;


        try {
            // InternalResoluteParser.g:2109:2: (iv_ruleRelationalExpr= ruleRelationalExpr EOF )
            // InternalResoluteParser.g:2110:2: iv_ruleRelationalExpr= ruleRelationalExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRelationalExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleRelationalExpr=ruleRelationalExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRelationalExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRelationalExpr"


    // $ANTLR start "ruleRelationalExpr"
    // InternalResoluteParser.g:2117:1: ruleRelationalExpr returns [EObject current=null] : (this_PlusExpr_0= rulePlusExpr ( ( ( ( () ( ( ruleRelationalOp ) ) ) )=> ( () ( (lv_op_2_0= ruleRelationalOp ) ) ) ) ( (lv_right_3_0= rulePlusExpr ) ) )? ) ;
    public final EObject ruleRelationalExpr() throws RecognitionException {
        EObject current = null;

        EObject this_PlusExpr_0 = null;

        AntlrDatatypeRuleToken lv_op_2_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:2120:28: ( (this_PlusExpr_0= rulePlusExpr ( ( ( ( () ( ( ruleRelationalOp ) ) ) )=> ( () ( (lv_op_2_0= ruleRelationalOp ) ) ) ) ( (lv_right_3_0= rulePlusExpr ) ) )? ) )
            // InternalResoluteParser.g:2121:1: (this_PlusExpr_0= rulePlusExpr ( ( ( ( () ( ( ruleRelationalOp ) ) ) )=> ( () ( (lv_op_2_0= ruleRelationalOp ) ) ) ) ( (lv_right_3_0= rulePlusExpr ) ) )? )
            {
            // InternalResoluteParser.g:2121:1: (this_PlusExpr_0= rulePlusExpr ( ( ( ( () ( ( ruleRelationalOp ) ) ) )=> ( () ( (lv_op_2_0= ruleRelationalOp ) ) ) ) ( (lv_right_3_0= rulePlusExpr ) ) )? )
            // InternalResoluteParser.g:2122:5: this_PlusExpr_0= rulePlusExpr ( ( ( ( () ( ( ruleRelationalOp ) ) ) )=> ( () ( (lv_op_2_0= ruleRelationalOp ) ) ) ) ( (lv_right_3_0= rulePlusExpr ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getRelationalExprAccess().getPlusExprParserRuleCall_0()); 
                  
            }
            pushFollow(FollowSets000.FOLLOW_30);
            this_PlusExpr_0=rulePlusExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current = this_PlusExpr_0;
                      afterParserOrEnumRuleCall();
                  
            }
            // InternalResoluteParser.g:2130:1: ( ( ( ( () ( ( ruleRelationalOp ) ) ) )=> ( () ( (lv_op_2_0= ruleRelationalOp ) ) ) ) ( (lv_right_3_0= rulePlusExpr ) ) )?
            int alt22=2;
            switch ( input.LA(1) ) {
                case LessThanSign:
                    {
                    int LA22_1 = input.LA(2);

                    if ( (synpred6_InternalResoluteParser()) ) {
                        alt22=1;
                    }
                    }
                    break;
                case LessThanSignEqualsSign:
                    {
                    int LA22_2 = input.LA(2);

                    if ( (synpred6_InternalResoluteParser()) ) {
                        alt22=1;
                    }
                    }
                    break;
                case GreaterThanSign:
                    {
                    int LA22_3 = input.LA(2);

                    if ( (synpred6_InternalResoluteParser()) ) {
                        alt22=1;
                    }
                    }
                    break;
                case GreaterThanSignEqualsSign:
                    {
                    int LA22_4 = input.LA(2);

                    if ( (synpred6_InternalResoluteParser()) ) {
                        alt22=1;
                    }
                    }
                    break;
                case EqualsSign:
                    {
                    int LA22_5 = input.LA(2);

                    if ( (synpred6_InternalResoluteParser()) ) {
                        alt22=1;
                    }
                    }
                    break;
                case LessThanSignGreaterThanSign:
                    {
                    int LA22_6 = input.LA(2);

                    if ( (synpred6_InternalResoluteParser()) ) {
                        alt22=1;
                    }
                    }
                    break;
            }

            switch (alt22) {
                case 1 :
                    // InternalResoluteParser.g:2130:2: ( ( ( () ( ( ruleRelationalOp ) ) ) )=> ( () ( (lv_op_2_0= ruleRelationalOp ) ) ) ) ( (lv_right_3_0= rulePlusExpr ) )
                    {
                    // InternalResoluteParser.g:2130:2: ( ( ( () ( ( ruleRelationalOp ) ) ) )=> ( () ( (lv_op_2_0= ruleRelationalOp ) ) ) )
                    // InternalResoluteParser.g:2130:3: ( ( () ( ( ruleRelationalOp ) ) ) )=> ( () ( (lv_op_2_0= ruleRelationalOp ) ) )
                    {
                    // InternalResoluteParser.g:2135:6: ( () ( (lv_op_2_0= ruleRelationalOp ) ) )
                    // InternalResoluteParser.g:2135:7: () ( (lv_op_2_0= ruleRelationalOp ) )
                    {
                    // InternalResoluteParser.g:2135:7: ()
                    // InternalResoluteParser.g:2136:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getRelationalExprAccess().getBinaryExprLeftAction_1_0_0_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:2141:2: ( (lv_op_2_0= ruleRelationalOp ) )
                    // InternalResoluteParser.g:2142:1: (lv_op_2_0= ruleRelationalOp )
                    {
                    // InternalResoluteParser.g:2142:1: (lv_op_2_0= ruleRelationalOp )
                    // InternalResoluteParser.g:2143:3: lv_op_2_0= ruleRelationalOp
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getRelationalExprAccess().getOpRelationalOpParserRuleCall_1_0_0_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_12);
                    lv_op_2_0=ruleRelationalOp();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getRelationalExprRule());
                      	        }
                             		set(
                             			current, 
                             			"op",
                              		lv_op_2_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.RelationalOp");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }

                    // InternalResoluteParser.g:2159:4: ( (lv_right_3_0= rulePlusExpr ) )
                    // InternalResoluteParser.g:2160:1: (lv_right_3_0= rulePlusExpr )
                    {
                    // InternalResoluteParser.g:2160:1: (lv_right_3_0= rulePlusExpr )
                    // InternalResoluteParser.g:2161:3: lv_right_3_0= rulePlusExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getRelationalExprAccess().getRightPlusExprParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_right_3_0=rulePlusExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getRelationalExprRule());
                      	        }
                             		set(
                             			current, 
                             			"right",
                              		lv_right_3_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.PlusExpr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationalExpr"


    // $ANTLR start "entryRulePlusExpr"
    // InternalResoluteParser.g:2185:1: entryRulePlusExpr returns [EObject current=null] : iv_rulePlusExpr= rulePlusExpr EOF ;
    public final EObject entryRulePlusExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePlusExpr = null;


        try {
            // InternalResoluteParser.g:2186:2: (iv_rulePlusExpr= rulePlusExpr EOF )
            // InternalResoluteParser.g:2187:2: iv_rulePlusExpr= rulePlusExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPlusExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_rulePlusExpr=rulePlusExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePlusExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePlusExpr"


    // $ANTLR start "rulePlusExpr"
    // InternalResoluteParser.g:2194:1: rulePlusExpr returns [EObject current=null] : (this_TimesExpr_0= ruleTimesExpr ( ( ( ( () ( ( ( PlusSign | HyphenMinus ) ) ) ) )=> ( () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) ) ) ) ( (lv_right_3_0= ruleTimesExpr ) ) )* ) ;
    public final EObject rulePlusExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        EObject this_TimesExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:2197:28: ( (this_TimesExpr_0= ruleTimesExpr ( ( ( ( () ( ( ( PlusSign | HyphenMinus ) ) ) ) )=> ( () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) ) ) ) ( (lv_right_3_0= ruleTimesExpr ) ) )* ) )
            // InternalResoluteParser.g:2198:1: (this_TimesExpr_0= ruleTimesExpr ( ( ( ( () ( ( ( PlusSign | HyphenMinus ) ) ) ) )=> ( () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) ) ) ) ( (lv_right_3_0= ruleTimesExpr ) ) )* )
            {
            // InternalResoluteParser.g:2198:1: (this_TimesExpr_0= ruleTimesExpr ( ( ( ( () ( ( ( PlusSign | HyphenMinus ) ) ) ) )=> ( () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) ) ) ) ( (lv_right_3_0= ruleTimesExpr ) ) )* )
            // InternalResoluteParser.g:2199:5: this_TimesExpr_0= ruleTimesExpr ( ( ( ( () ( ( ( PlusSign | HyphenMinus ) ) ) ) )=> ( () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) ) ) ) ( (lv_right_3_0= ruleTimesExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getPlusExprAccess().getTimesExprParserRuleCall_0()); 
                  
            }
            pushFollow(FollowSets000.FOLLOW_31);
            this_TimesExpr_0=ruleTimesExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current = this_TimesExpr_0;
                      afterParserOrEnumRuleCall();
                  
            }
            // InternalResoluteParser.g:2207:1: ( ( ( ( () ( ( ( PlusSign | HyphenMinus ) ) ) ) )=> ( () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) ) ) ) ( (lv_right_3_0= ruleTimesExpr ) ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==PlusSign) ) {
                    int LA24_2 = input.LA(2);

                    if ( (synpred7_InternalResoluteParser()) ) {
                        alt24=1;
                    }


                }
                else if ( (LA24_0==HyphenMinus) ) {
                    int LA24_3 = input.LA(2);

                    if ( (synpred7_InternalResoluteParser()) ) {
                        alt24=1;
                    }


                }


                switch (alt24) {
            	case 1 :
            	    // InternalResoluteParser.g:2207:2: ( ( ( () ( ( ( PlusSign | HyphenMinus ) ) ) ) )=> ( () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) ) ) ) ( (lv_right_3_0= ruleTimesExpr ) )
            	    {
            	    // InternalResoluteParser.g:2207:2: ( ( ( () ( ( ( PlusSign | HyphenMinus ) ) ) ) )=> ( () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) ) ) )
            	    // InternalResoluteParser.g:2207:3: ( ( () ( ( ( PlusSign | HyphenMinus ) ) ) ) )=> ( () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) ) )
            	    {
            	    // InternalResoluteParser.g:2222:6: ( () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) ) )
            	    // InternalResoluteParser.g:2222:7: () ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) )
            	    {
            	    // InternalResoluteParser.g:2222:7: ()
            	    // InternalResoluteParser.g:2223:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getPlusExprAccess().getBinaryExprLeftAction_1_0_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // InternalResoluteParser.g:2228:2: ( ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) ) )
            	    // InternalResoluteParser.g:2229:1: ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) )
            	    {
            	    // InternalResoluteParser.g:2229:1: ( (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus ) )
            	    // InternalResoluteParser.g:2230:1: (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus )
            	    {
            	    // InternalResoluteParser.g:2230:1: (lv_op_2_1= PlusSign | lv_op_2_2= HyphenMinus )
            	    int alt23=2;
            	    int LA23_0 = input.LA(1);

            	    if ( (LA23_0==PlusSign) ) {
            	        alt23=1;
            	    }
            	    else if ( (LA23_0==HyphenMinus) ) {
            	        alt23=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 23, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt23) {
            	        case 1 :
            	            // InternalResoluteParser.g:2231:3: lv_op_2_1= PlusSign
            	            {
            	            lv_op_2_1=(Token)match(input,PlusSign,FollowSets000.FOLLOW_12); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_1, grammarAccess.getPlusExprAccess().getOpPlusSignKeyword_1_0_0_1_0_0());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getPlusExprRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_1, null);
            	              	    
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalResoluteParser.g:2244:8: lv_op_2_2= HyphenMinus
            	            {
            	            lv_op_2_2=(Token)match(input,HyphenMinus,FollowSets000.FOLLOW_12); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_2, grammarAccess.getPlusExprAccess().getOpHyphenMinusKeyword_1_0_0_1_0_1());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getPlusExprRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_2, null);
            	              	    
            	            }

            	            }
            	            break;

            	    }


            	    }


            	    }


            	    }


            	    }

            	    // InternalResoluteParser.g:2260:4: ( (lv_right_3_0= ruleTimesExpr ) )
            	    // InternalResoluteParser.g:2261:1: (lv_right_3_0= ruleTimesExpr )
            	    {
            	    // InternalResoluteParser.g:2261:1: (lv_right_3_0= ruleTimesExpr )
            	    // InternalResoluteParser.g:2262:3: lv_right_3_0= ruleTimesExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getPlusExprAccess().getRightTimesExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_31);
            	    lv_right_3_0=ruleTimesExpr();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getPlusExprRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"com.rockwellcollins.atc.resolute.Resolute.TimesExpr");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePlusExpr"


    // $ANTLR start "entryRuleTimesExpr"
    // InternalResoluteParser.g:2286:1: entryRuleTimesExpr returns [EObject current=null] : iv_ruleTimesExpr= ruleTimesExpr EOF ;
    public final EObject entryRuleTimesExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTimesExpr = null;


        try {
            // InternalResoluteParser.g:2287:2: (iv_ruleTimesExpr= ruleTimesExpr EOF )
            // InternalResoluteParser.g:2288:2: iv_ruleTimesExpr= ruleTimesExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTimesExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleTimesExpr=ruleTimesExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTimesExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTimesExpr"


    // $ANTLR start "ruleTimesExpr"
    // InternalResoluteParser.g:2295:1: ruleTimesExpr returns [EObject current=null] : (this_ExpExpr_0= ruleExpExpr ( ( ( ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) ) )=> ( () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) ) ) ) ( (lv_right_3_0= ruleExpExpr ) ) )* ) ;
    public final EObject ruleTimesExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        Token lv_op_2_3=null;
        EObject this_ExpExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:2298:28: ( (this_ExpExpr_0= ruleExpExpr ( ( ( ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) ) )=> ( () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) ) ) ) ( (lv_right_3_0= ruleExpExpr ) ) )* ) )
            // InternalResoluteParser.g:2299:1: (this_ExpExpr_0= ruleExpExpr ( ( ( ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) ) )=> ( () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) ) ) ) ( (lv_right_3_0= ruleExpExpr ) ) )* )
            {
            // InternalResoluteParser.g:2299:1: (this_ExpExpr_0= ruleExpExpr ( ( ( ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) ) )=> ( () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) ) ) ) ( (lv_right_3_0= ruleExpExpr ) ) )* )
            // InternalResoluteParser.g:2300:5: this_ExpExpr_0= ruleExpExpr ( ( ( ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) ) )=> ( () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) ) ) ) ( (lv_right_3_0= ruleExpExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getTimesExprAccess().getExpExprParserRuleCall_0()); 
                  
            }
            pushFollow(FollowSets000.FOLLOW_32);
            this_ExpExpr_0=ruleExpExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current = this_ExpExpr_0;
                      afterParserOrEnumRuleCall();
                  
            }
            // InternalResoluteParser.g:2308:1: ( ( ( ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) ) )=> ( () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) ) ) ) ( (lv_right_3_0= ruleExpExpr ) ) )*
            loop26:
            do {
                int alt26=2;
                switch ( input.LA(1) ) {
                case Asterisk:
                    {
                    int LA26_2 = input.LA(2);

                    if ( (synpred8_InternalResoluteParser()) ) {
                        alt26=1;
                    }


                    }
                    break;
                case Solidus:
                    {
                    int LA26_3 = input.LA(2);

                    if ( (synpred8_InternalResoluteParser()) ) {
                        alt26=1;
                    }


                    }
                    break;
                case PercentSign:
                    {
                    int LA26_4 = input.LA(2);

                    if ( (synpred8_InternalResoluteParser()) ) {
                        alt26=1;
                    }


                    }
                    break;

                }

                switch (alt26) {
            	case 1 :
            	    // InternalResoluteParser.g:2308:2: ( ( ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) ) )=> ( () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) ) ) ) ( (lv_right_3_0= ruleExpExpr ) )
            	    {
            	    // InternalResoluteParser.g:2308:2: ( ( ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) ) )=> ( () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) ) ) )
            	    // InternalResoluteParser.g:2308:3: ( ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) ) )=> ( () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) ) )
            	    {
            	    // InternalResoluteParser.g:2327:6: ( () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) ) )
            	    // InternalResoluteParser.g:2327:7: () ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) )
            	    {
            	    // InternalResoluteParser.g:2327:7: ()
            	    // InternalResoluteParser.g:2328:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getTimesExprAccess().getBinaryExprLeftAction_1_0_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // InternalResoluteParser.g:2333:2: ( ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) ) )
            	    // InternalResoluteParser.g:2334:1: ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) )
            	    {
            	    // InternalResoluteParser.g:2334:1: ( (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign ) )
            	    // InternalResoluteParser.g:2335:1: (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign )
            	    {
            	    // InternalResoluteParser.g:2335:1: (lv_op_2_1= Asterisk | lv_op_2_2= Solidus | lv_op_2_3= PercentSign )
            	    int alt25=3;
            	    switch ( input.LA(1) ) {
            	    case Asterisk:
            	        {
            	        alt25=1;
            	        }
            	        break;
            	    case Solidus:
            	        {
            	        alt25=2;
            	        }
            	        break;
            	    case PercentSign:
            	        {
            	        alt25=3;
            	        }
            	        break;
            	    default:
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 25, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt25) {
            	        case 1 :
            	            // InternalResoluteParser.g:2336:3: lv_op_2_1= Asterisk
            	            {
            	            lv_op_2_1=(Token)match(input,Asterisk,FollowSets000.FOLLOW_12); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_1, grammarAccess.getTimesExprAccess().getOpAsteriskKeyword_1_0_0_1_0_0());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getTimesExprRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_1, null);
            	              	    
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalResoluteParser.g:2349:8: lv_op_2_2= Solidus
            	            {
            	            lv_op_2_2=(Token)match(input,Solidus,FollowSets000.FOLLOW_12); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_2, grammarAccess.getTimesExprAccess().getOpSolidusKeyword_1_0_0_1_0_1());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getTimesExprRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_2, null);
            	              	    
            	            }

            	            }
            	            break;
            	        case 3 :
            	            // InternalResoluteParser.g:2362:8: lv_op_2_3= PercentSign
            	            {
            	            lv_op_2_3=(Token)match(input,PercentSign,FollowSets000.FOLLOW_12); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_3, grammarAccess.getTimesExprAccess().getOpPercentSignKeyword_1_0_0_1_0_2());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getTimesExprRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_3, null);
            	              	    
            	            }

            	            }
            	            break;

            	    }


            	    }


            	    }


            	    }


            	    }

            	    // InternalResoluteParser.g:2378:4: ( (lv_right_3_0= ruleExpExpr ) )
            	    // InternalResoluteParser.g:2379:1: (lv_right_3_0= ruleExpExpr )
            	    {
            	    // InternalResoluteParser.g:2379:1: (lv_right_3_0= ruleExpExpr )
            	    // InternalResoluteParser.g:2380:3: lv_right_3_0= ruleExpExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getTimesExprAccess().getRightExpExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_32);
            	    lv_right_3_0=ruleExpExpr();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getTimesExprRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"com.rockwellcollins.atc.resolute.Resolute.ExpExpr");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTimesExpr"


    // $ANTLR start "entryRuleExpExpr"
    // InternalResoluteParser.g:2404:1: entryRuleExpExpr returns [EObject current=null] : iv_ruleExpExpr= ruleExpExpr EOF ;
    public final EObject entryRuleExpExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpExpr = null;


        try {
            // InternalResoluteParser.g:2405:2: (iv_ruleExpExpr= ruleExpExpr EOF )
            // InternalResoluteParser.g:2406:2: iv_ruleExpExpr= ruleExpExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExpExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleExpExpr=ruleExpExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExpExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleExpExpr"


    // $ANTLR start "ruleExpExpr"
    // InternalResoluteParser.g:2413:1: ruleExpExpr returns [EObject current=null] : (this_PrefixExpr_0= rulePrefixExpr ( ( ( ( () ( ( CircumflexAccent ) ) ) )=> ( () ( (lv_op_2_0= CircumflexAccent ) ) ) ) ( (lv_right_3_0= rulePrefixExpr ) ) )* ) ;
    public final EObject ruleExpExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_PrefixExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:2416:28: ( (this_PrefixExpr_0= rulePrefixExpr ( ( ( ( () ( ( CircumflexAccent ) ) ) )=> ( () ( (lv_op_2_0= CircumflexAccent ) ) ) ) ( (lv_right_3_0= rulePrefixExpr ) ) )* ) )
            // InternalResoluteParser.g:2417:1: (this_PrefixExpr_0= rulePrefixExpr ( ( ( ( () ( ( CircumflexAccent ) ) ) )=> ( () ( (lv_op_2_0= CircumflexAccent ) ) ) ) ( (lv_right_3_0= rulePrefixExpr ) ) )* )
            {
            // InternalResoluteParser.g:2417:1: (this_PrefixExpr_0= rulePrefixExpr ( ( ( ( () ( ( CircumflexAccent ) ) ) )=> ( () ( (lv_op_2_0= CircumflexAccent ) ) ) ) ( (lv_right_3_0= rulePrefixExpr ) ) )* )
            // InternalResoluteParser.g:2418:5: this_PrefixExpr_0= rulePrefixExpr ( ( ( ( () ( ( CircumflexAccent ) ) ) )=> ( () ( (lv_op_2_0= CircumflexAccent ) ) ) ) ( (lv_right_3_0= rulePrefixExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getExpExprAccess().getPrefixExprParserRuleCall_0()); 
                  
            }
            pushFollow(FollowSets000.FOLLOW_33);
            this_PrefixExpr_0=rulePrefixExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current = this_PrefixExpr_0;
                      afterParserOrEnumRuleCall();
                  
            }
            // InternalResoluteParser.g:2426:1: ( ( ( ( () ( ( CircumflexAccent ) ) ) )=> ( () ( (lv_op_2_0= CircumflexAccent ) ) ) ) ( (lv_right_3_0= rulePrefixExpr ) ) )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==CircumflexAccent) ) {
                    int LA27_2 = input.LA(2);

                    if ( (synpred9_InternalResoluteParser()) ) {
                        alt27=1;
                    }


                }


                switch (alt27) {
            	case 1 :
            	    // InternalResoluteParser.g:2426:2: ( ( ( () ( ( CircumflexAccent ) ) ) )=> ( () ( (lv_op_2_0= CircumflexAccent ) ) ) ) ( (lv_right_3_0= rulePrefixExpr ) )
            	    {
            	    // InternalResoluteParser.g:2426:2: ( ( ( () ( ( CircumflexAccent ) ) ) )=> ( () ( (lv_op_2_0= CircumflexAccent ) ) ) )
            	    // InternalResoluteParser.g:2426:3: ( ( () ( ( CircumflexAccent ) ) ) )=> ( () ( (lv_op_2_0= CircumflexAccent ) ) )
            	    {
            	    // InternalResoluteParser.g:2434:6: ( () ( (lv_op_2_0= CircumflexAccent ) ) )
            	    // InternalResoluteParser.g:2434:7: () ( (lv_op_2_0= CircumflexAccent ) )
            	    {
            	    // InternalResoluteParser.g:2434:7: ()
            	    // InternalResoluteParser.g:2435:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getExpExprAccess().getBinaryExprLeftAction_1_0_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // InternalResoluteParser.g:2440:2: ( (lv_op_2_0= CircumflexAccent ) )
            	    // InternalResoluteParser.g:2441:1: (lv_op_2_0= CircumflexAccent )
            	    {
            	    // InternalResoluteParser.g:2441:1: (lv_op_2_0= CircumflexAccent )
            	    // InternalResoluteParser.g:2442:3: lv_op_2_0= CircumflexAccent
            	    {
            	    lv_op_2_0=(Token)match(input,CircumflexAccent,FollowSets000.FOLLOW_12); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	              newLeafNode(lv_op_2_0, grammarAccess.getExpExprAccess().getOpCircumflexAccentKeyword_1_0_0_1_0());
            	          
            	    }
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElement(grammarAccess.getExpExprRule());
            	      	        }
            	             		setWithLastConsumed(current, "op", lv_op_2_0, "^");
            	      	    
            	    }

            	    }


            	    }


            	    }


            	    }

            	    // InternalResoluteParser.g:2456:4: ( (lv_right_3_0= rulePrefixExpr ) )
            	    // InternalResoluteParser.g:2457:1: (lv_right_3_0= rulePrefixExpr )
            	    {
            	    // InternalResoluteParser.g:2457:1: (lv_right_3_0= rulePrefixExpr )
            	    // InternalResoluteParser.g:2458:3: lv_right_3_0= rulePrefixExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getExpExprAccess().getRightPrefixExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_33);
            	    lv_right_3_0=rulePrefixExpr();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getExpExprRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"com.rockwellcollins.atc.resolute.Resolute.PrefixExpr");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleExpExpr"


    // $ANTLR start "entryRulePrefixExpr"
    // InternalResoluteParser.g:2482:1: entryRulePrefixExpr returns [EObject current=null] : iv_rulePrefixExpr= rulePrefixExpr EOF ;
    public final EObject entryRulePrefixExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrefixExpr = null;


        try {
            // InternalResoluteParser.g:2483:2: (iv_rulePrefixExpr= rulePrefixExpr EOF )
            // InternalResoluteParser.g:2484:2: iv_rulePrefixExpr= rulePrefixExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPrefixExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_rulePrefixExpr=rulePrefixExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePrefixExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePrefixExpr"


    // $ANTLR start "rulePrefixExpr"
    // InternalResoluteParser.g:2491:1: rulePrefixExpr returns [EObject current=null] : ( ( () ( ( (lv_op_1_1= HyphenMinus | lv_op_1_2= Not ) ) ) ( (lv_expr_2_0= rulePrefixExpr ) ) ) | ( () otherlv_4= LeftParenthesis ( (lv_type_5_0= ruleBaseType ) ) otherlv_6= RightParenthesis ( (lv_expr_7_0= rulePrefixExpr ) ) ) | this_AtomicExpr_8= ruleAtomicExpr ) ;
    public final EObject rulePrefixExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_1_1=null;
        Token lv_op_1_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        EObject lv_expr_2_0 = null;

        EObject lv_type_5_0 = null;

        EObject lv_expr_7_0 = null;

        EObject this_AtomicExpr_8 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:2494:28: ( ( ( () ( ( (lv_op_1_1= HyphenMinus | lv_op_1_2= Not ) ) ) ( (lv_expr_2_0= rulePrefixExpr ) ) ) | ( () otherlv_4= LeftParenthesis ( (lv_type_5_0= ruleBaseType ) ) otherlv_6= RightParenthesis ( (lv_expr_7_0= rulePrefixExpr ) ) ) | this_AtomicExpr_8= ruleAtomicExpr ) )
            // InternalResoluteParser.g:2495:1: ( ( () ( ( (lv_op_1_1= HyphenMinus | lv_op_1_2= Not ) ) ) ( (lv_expr_2_0= rulePrefixExpr ) ) ) | ( () otherlv_4= LeftParenthesis ( (lv_type_5_0= ruleBaseType ) ) otherlv_6= RightParenthesis ( (lv_expr_7_0= rulePrefixExpr ) ) ) | this_AtomicExpr_8= ruleAtomicExpr )
            {
            // InternalResoluteParser.g:2495:1: ( ( () ( ( (lv_op_1_1= HyphenMinus | lv_op_1_2= Not ) ) ) ( (lv_expr_2_0= rulePrefixExpr ) ) ) | ( () otherlv_4= LeftParenthesis ( (lv_type_5_0= ruleBaseType ) ) otherlv_6= RightParenthesis ( (lv_expr_7_0= rulePrefixExpr ) ) ) | this_AtomicExpr_8= ruleAtomicExpr )
            int alt29=3;
            switch ( input.LA(1) ) {
            case Not:
            case HyphenMinus:
                {
                alt29=1;
                }
                break;
            case LeftParenthesis:
                {
                switch ( input.LA(2) ) {
                case Provides_subprogram_group_access:
                case Requires_subprogram_group_access:
                case Provides_subprogram_access:
                case Requires_subprogram_access:
                case Subprogram_group_access:
                case Provides_data_access:
                case Requires_data_access:
                case Provides_bus_access:
                case Requires_bus_access:
                case Flow_specification:
                case Subprogram_access:
                case Virtual_processor:
                case Subprogram_group:
                case End_to_end_flow:
                case Event_data_port:
                case Feature_group:
                case Thread_group:
                case Data_access:
                case Virtual_bus:
                case Bus_access:
                case Connection:
                case Event_port:
                case Subprogram:
                case Component:
                case Data_port:
                case Processor:
                case Abstract:
                case Feature:
                case Process:
                case Access:
                case Device:
                case Memory:
                case String:
                case KW_System:
                case Thread:
                case Range:
                case Aadl:
                case Bool:
                case Data:
                case Port:
                case Real:
                case Bus:
                case Int:
                    {
                    alt29=2;
                    }
                    break;
                case Property:
                    {
                    int LA29_5 = input.LA(3);

                    if ( (LA29_5==LeftParenthesis) ) {
                        alt29=3;
                    }
                    else if ( (LA29_5==RightParenthesis) ) {
                        alt29=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 29, 5, input);

                        throw nvae;
                    }
                    }
                    break;
                case Error_state_reachable:
                case Is_virtual_processor:
                case Flow_specifications:
                case Is_abstract_feature:
                case Enumerated_values:
                case End_to_end_flows:
                case Flow_destination:
                case Is_bidirectional:
                case Is_thread_group:
                case Propagate_error:
                case Property_member:
                case Has_prototypes:
                case Is_data_access:
                case Is_virtual_bus:
                case Contain_error:
                case Flow_elements:
                case Is_bus_access:
                case Is_event_port:
                case Is_subprogram:
                case Receive_error:
                case Subcomponents:
                case Has_property:
                case Is_data_port:
                case Is_processor:
                case Connections:
                case Destination:
                case Flow_source:
                case Is_bound_to:
                case Is_in_array:
                case Lower_bound:
                case Upper_bound:
                case Has_member:
                case Has_parent:
                case Is_of_type:
                case Is_process:
                case Direction:
                case Has_modes:
                case Instances:
                case Intersect:
                case Is_device:
                case Is_memory:
                case Is_system:
                case Is_thread:
                case Analysis:
                case Features:
                case Has_type:
                case Instance:
                case As_list:
                case Is_data:
                case Is_port:
                case Warning:
                case Append:
                case As_set:
                case Exists:
                case Forall:
                case Is_bus:
                case Length:
                case Member:
                case Parent:
                case Source:
                case Debug:
                case Error:
                case False:
                case Union:
                case Fail:
                case Head:
                case Info:
                case Name:
                case Size:
                case Tail:
                case This:
                case True:
                case Type:
                case Let:
                case Not:
                case Sum:
                case If:
                case LeftParenthesis:
                case HyphenMinus:
                case LeftSquareBracket:
                case LeftCurlyBracket:
                case RULE_REAL_LIT:
                case RULE_INTEGER_LIT:
                case RULE_STRING:
                case RULE_ID:
                    {
                    alt29=3;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 2, input);

                    throw nvae;
                }

                }
                break;
            case Error_state_reachable:
            case Is_virtual_processor:
            case Flow_specifications:
            case Is_abstract_feature:
            case Enumerated_values:
            case End_to_end_flows:
            case Flow_destination:
            case Is_bidirectional:
            case Is_thread_group:
            case Propagate_error:
            case Property_member:
            case Has_prototypes:
            case Is_data_access:
            case Is_virtual_bus:
            case Contain_error:
            case Flow_elements:
            case Is_bus_access:
            case Is_event_port:
            case Is_subprogram:
            case Receive_error:
            case Subcomponents:
            case Has_property:
            case Is_data_port:
            case Is_processor:
            case Connections:
            case Destination:
            case Flow_source:
            case Is_bound_to:
            case Is_in_array:
            case Lower_bound:
            case Upper_bound:
            case Has_member:
            case Has_parent:
            case Is_of_type:
            case Is_process:
            case Direction:
            case Has_modes:
            case Instances:
            case Intersect:
            case Is_device:
            case Is_memory:
            case Is_system:
            case Is_thread:
            case Analysis:
            case Features:
            case Has_type:
            case Instance:
            case Property:
            case As_list:
            case Is_data:
            case Is_port:
            case Warning:
            case Append:
            case As_set:
            case Exists:
            case Forall:
            case Is_bus:
            case Length:
            case Member:
            case Parent:
            case Source:
            case Debug:
            case Error:
            case False:
            case Union:
            case Fail:
            case Head:
            case Info:
            case Name:
            case Size:
            case Tail:
            case This:
            case True:
            case Type:
            case Let:
            case Sum:
            case If:
            case LeftSquareBracket:
            case LeftCurlyBracket:
            case RULE_REAL_LIT:
            case RULE_INTEGER_LIT:
            case RULE_STRING:
            case RULE_ID:
                {
                alt29=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // InternalResoluteParser.g:2495:2: ( () ( ( (lv_op_1_1= HyphenMinus | lv_op_1_2= Not ) ) ) ( (lv_expr_2_0= rulePrefixExpr ) ) )
                    {
                    // InternalResoluteParser.g:2495:2: ( () ( ( (lv_op_1_1= HyphenMinus | lv_op_1_2= Not ) ) ) ( (lv_expr_2_0= rulePrefixExpr ) ) )
                    // InternalResoluteParser.g:2495:3: () ( ( (lv_op_1_1= HyphenMinus | lv_op_1_2= Not ) ) ) ( (lv_expr_2_0= rulePrefixExpr ) )
                    {
                    // InternalResoluteParser.g:2495:3: ()
                    // InternalResoluteParser.g:2496:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getPrefixExprAccess().getUnaryExprAction_0_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:2501:2: ( ( (lv_op_1_1= HyphenMinus | lv_op_1_2= Not ) ) )
                    // InternalResoluteParser.g:2502:1: ( (lv_op_1_1= HyphenMinus | lv_op_1_2= Not ) )
                    {
                    // InternalResoluteParser.g:2502:1: ( (lv_op_1_1= HyphenMinus | lv_op_1_2= Not ) )
                    // InternalResoluteParser.g:2503:1: (lv_op_1_1= HyphenMinus | lv_op_1_2= Not )
                    {
                    // InternalResoluteParser.g:2503:1: (lv_op_1_1= HyphenMinus | lv_op_1_2= Not )
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==HyphenMinus) ) {
                        alt28=1;
                    }
                    else if ( (LA28_0==Not) ) {
                        alt28=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 28, 0, input);

                        throw nvae;
                    }
                    switch (alt28) {
                        case 1 :
                            // InternalResoluteParser.g:2504:3: lv_op_1_1= HyphenMinus
                            {
                            lv_op_1_1=(Token)match(input,HyphenMinus,FollowSets000.FOLLOW_12); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_1_1, grammarAccess.getPrefixExprAccess().getOpHyphenMinusKeyword_0_1_0_0());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getPrefixExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_1_1, null);
                              	    
                            }

                            }
                            break;
                        case 2 :
                            // InternalResoluteParser.g:2517:8: lv_op_1_2= Not
                            {
                            lv_op_1_2=(Token)match(input,Not,FollowSets000.FOLLOW_12); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_1_2, grammarAccess.getPrefixExprAccess().getOpNotKeyword_0_1_0_1());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getPrefixExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_1_2, null);
                              	    
                            }

                            }
                            break;

                    }


                    }


                    }

                    // InternalResoluteParser.g:2533:2: ( (lv_expr_2_0= rulePrefixExpr ) )
                    // InternalResoluteParser.g:2534:1: (lv_expr_2_0= rulePrefixExpr )
                    {
                    // InternalResoluteParser.g:2534:1: (lv_expr_2_0= rulePrefixExpr )
                    // InternalResoluteParser.g:2535:3: lv_expr_2_0= rulePrefixExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getPrefixExprAccess().getExprPrefixExprParserRuleCall_0_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_2_0=rulePrefixExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getPrefixExprRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_2_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.PrefixExpr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:2552:6: ( () otherlv_4= LeftParenthesis ( (lv_type_5_0= ruleBaseType ) ) otherlv_6= RightParenthesis ( (lv_expr_7_0= rulePrefixExpr ) ) )
                    {
                    // InternalResoluteParser.g:2552:6: ( () otherlv_4= LeftParenthesis ( (lv_type_5_0= ruleBaseType ) ) otherlv_6= RightParenthesis ( (lv_expr_7_0= rulePrefixExpr ) ) )
                    // InternalResoluteParser.g:2552:7: () otherlv_4= LeftParenthesis ( (lv_type_5_0= ruleBaseType ) ) otherlv_6= RightParenthesis ( (lv_expr_7_0= rulePrefixExpr ) )
                    {
                    // InternalResoluteParser.g:2552:7: ()
                    // InternalResoluteParser.g:2553:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getPrefixExprAccess().getCastExprAction_1_0(),
                                  current);
                          
                    }

                    }

                    otherlv_4=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_29); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getPrefixExprAccess().getLeftParenthesisKeyword_1_1());
                          
                    }
                    // InternalResoluteParser.g:2563:1: ( (lv_type_5_0= ruleBaseType ) )
                    // InternalResoluteParser.g:2564:1: (lv_type_5_0= ruleBaseType )
                    {
                    // InternalResoluteParser.g:2564:1: (lv_type_5_0= ruleBaseType )
                    // InternalResoluteParser.g:2565:3: lv_type_5_0= ruleBaseType
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getPrefixExprAccess().getTypeBaseTypeParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_34);
                    lv_type_5_0=ruleBaseType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getPrefixExprRule());
                      	        }
                             		set(
                             			current, 
                             			"type",
                              		lv_type_5_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.BaseType");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_6=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_6, grammarAccess.getPrefixExprAccess().getRightParenthesisKeyword_1_3());
                          
                    }
                    // InternalResoluteParser.g:2586:1: ( (lv_expr_7_0= rulePrefixExpr ) )
                    // InternalResoluteParser.g:2587:1: (lv_expr_7_0= rulePrefixExpr )
                    {
                    // InternalResoluteParser.g:2587:1: (lv_expr_7_0= rulePrefixExpr )
                    // InternalResoluteParser.g:2588:3: lv_expr_7_0= rulePrefixExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getPrefixExprAccess().getExprPrefixExprParserRuleCall_1_4_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_7_0=rulePrefixExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getPrefixExprRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_7_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.PrefixExpr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalResoluteParser.g:2606:5: this_AtomicExpr_8= ruleAtomicExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrefixExprAccess().getAtomicExprParserRuleCall_2()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AtomicExpr_8=ruleAtomicExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_AtomicExpr_8;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePrefixExpr"


    // $ANTLR start "entryRuleAtomicExpr"
    // InternalResoluteParser.g:2622:1: entryRuleAtomicExpr returns [EObject current=null] : iv_ruleAtomicExpr= ruleAtomicExpr EOF ;
    public final EObject entryRuleAtomicExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAtomicExpr = null;


        try {
            // InternalResoluteParser.g:2623:2: (iv_ruleAtomicExpr= ruleAtomicExpr EOF )
            // InternalResoluteParser.g:2624:2: iv_ruleAtomicExpr= ruleAtomicExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAtomicExprRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAtomicExpr=ruleAtomicExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAtomicExpr; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAtomicExpr"


    // $ANTLR start "ruleAtomicExpr"
    // InternalResoluteParser.g:2631:1: ruleAtomicExpr returns [EObject current=null] : ( ( () ( ( ruleQCREF ) ) ) | ( () otherlv_3= This (otherlv_4= FullStop ( (lv_sub_5_0= ruleNestedDotID ) ) )? ) | ( () otherlv_7= Fail ( ( (lv_val_8_0= ruleExpr ) ) | (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk ) ) ) | ( () ( (lv_val_13_0= ruleIntegerTerm ) ) ) | ( () ( (lv_val_15_0= ruleRealTerm ) ) ) | ( () ( (lv_val_17_0= ruleBooleanLiteral ) ) ) | ( () ( (lv_val_19_0= ruleStringTerm ) ) ) | ( () otherlv_21= If ( (lv_cond_22_0= ruleExpr ) ) otherlv_23= Then ( (lv_then_24_0= ruleExpr ) ) otherlv_25= Else ( (lv_else_26_0= ruleExpr ) ) ) | ( () ( ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) ) ) (otherlv_29= LeftParenthesis ( (lv_args_30_0= ruleArg ) ) otherlv_31= RightParenthesis )+ otherlv_32= FullStop ( (lv_expr_33_0= ruleExpr ) ) ) | ( ( ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis ) )=> ( () ( (lv_libName_35_0= RULE_ID ) ) otherlv_36= FullStop ( (lv_fnName_37_0= RULE_ID ) ) otherlv_38= LeftParenthesis ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )? otherlv_42= RightParenthesis ) ) | ( () ( (lv_fn_44_0= ruleBuiltInFn ) ) otherlv_45= LeftParenthesis ( ( (lv_args_46_0= ruleExpr ) ) (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )* )? otherlv_49= RightParenthesis ) | ( () ( (otherlv_51= RULE_ID ) ) otherlv_52= LeftParenthesis ( ( (lv_args_53_0= ruleExpr ) ) (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )* )? otherlv_56= RightParenthesis ) | ( () ( (lv_lintStmt_58_0= ruleLintStatement ) ) ) | (otherlv_59= LeftSquareBracket this_Expr_60= ruleExpr ( ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? ) | ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* ) ) otherlv_71= RightSquareBracket ) | (otherlv_72= LeftCurlyBracket this_Expr_73= ruleExpr ( ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? ) | ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* ) ) otherlv_84= RightCurlyBracket ) | ( () otherlv_86= LeftSquareBracket otherlv_87= RightSquareBracket ) | ( () otherlv_89= LeftCurlyBracket otherlv_90= RightCurlyBracket ) | ( () otherlv_92= Let ( (lv_binding_93_0= ruleLetBinding ) ) otherlv_94= Semicolon ( (lv_expr_95_0= ruleExpr ) ) ) | (otherlv_96= LeftParenthesis this_Expr_97= ruleExpr otherlv_98= RightParenthesis ) ) ;
    public final EObject ruleAtomicExpr() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_21=null;
        Token otherlv_23=null;
        Token otherlv_25=null;
        Token lv_quant_28_1=null;
        Token lv_quant_28_2=null;
        Token otherlv_29=null;
        Token otherlv_31=null;
        Token otherlv_32=null;
        Token lv_libName_35_0=null;
        Token otherlv_36=null;
        Token lv_fnName_37_0=null;
        Token otherlv_38=null;
        Token otherlv_40=null;
        Token otherlv_42=null;
        Token otherlv_45=null;
        Token otherlv_47=null;
        Token otherlv_49=null;
        Token otherlv_51=null;
        Token otherlv_52=null;
        Token otherlv_54=null;
        Token otherlv_56=null;
        Token otherlv_59=null;
        Token otherlv_62=null;
        Token otherlv_63=null;
        Token otherlv_65=null;
        Token otherlv_66=null;
        Token otherlv_69=null;
        Token otherlv_71=null;
        Token otherlv_72=null;
        Token otherlv_75=null;
        Token otherlv_76=null;
        Token otherlv_78=null;
        Token otherlv_79=null;
        Token otherlv_82=null;
        Token otherlv_84=null;
        Token otherlv_86=null;
        Token otherlv_87=null;
        Token otherlv_89=null;
        Token otherlv_90=null;
        Token otherlv_92=null;
        Token otherlv_94=null;
        Token otherlv_96=null;
        Token otherlv_98=null;
        EObject lv_sub_5_0 = null;

        EObject lv_val_8_0 = null;

        EObject lv_failmsg_10_0 = null;

        EObject lv_val_13_0 = null;

        EObject lv_val_15_0 = null;

        EObject lv_val_17_0 = null;

        EObject lv_val_19_0 = null;

        EObject lv_cond_22_0 = null;

        EObject lv_then_24_0 = null;

        EObject lv_else_26_0 = null;

        EObject lv_args_30_0 = null;

        EObject lv_expr_33_0 = null;

        EObject lv_args_39_0 = null;

        EObject lv_args_41_0 = null;

        AntlrDatatypeRuleToken lv_fn_44_0 = null;

        EObject lv_args_46_0 = null;

        EObject lv_args_48_0 = null;

        EObject lv_args_53_0 = null;

        EObject lv_args_55_0 = null;

        EObject lv_lintStmt_58_0 = null;

        EObject this_Expr_60 = null;

        EObject lv_args_64_0 = null;

        EObject lv_filter_67_0 = null;

        EObject lv_exprs_70_0 = null;

        EObject this_Expr_73 = null;

        EObject lv_args_77_0 = null;

        EObject lv_filter_80_0 = null;

        EObject lv_exprs_83_0 = null;

        EObject lv_binding_93_0 = null;

        EObject lv_expr_95_0 = null;

        EObject this_Expr_97 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:2634:28: ( ( ( () ( ( ruleQCREF ) ) ) | ( () otherlv_3= This (otherlv_4= FullStop ( (lv_sub_5_0= ruleNestedDotID ) ) )? ) | ( () otherlv_7= Fail ( ( (lv_val_8_0= ruleExpr ) ) | (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk ) ) ) | ( () ( (lv_val_13_0= ruleIntegerTerm ) ) ) | ( () ( (lv_val_15_0= ruleRealTerm ) ) ) | ( () ( (lv_val_17_0= ruleBooleanLiteral ) ) ) | ( () ( (lv_val_19_0= ruleStringTerm ) ) ) | ( () otherlv_21= If ( (lv_cond_22_0= ruleExpr ) ) otherlv_23= Then ( (lv_then_24_0= ruleExpr ) ) otherlv_25= Else ( (lv_else_26_0= ruleExpr ) ) ) | ( () ( ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) ) ) (otherlv_29= LeftParenthesis ( (lv_args_30_0= ruleArg ) ) otherlv_31= RightParenthesis )+ otherlv_32= FullStop ( (lv_expr_33_0= ruleExpr ) ) ) | ( ( ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis ) )=> ( () ( (lv_libName_35_0= RULE_ID ) ) otherlv_36= FullStop ( (lv_fnName_37_0= RULE_ID ) ) otherlv_38= LeftParenthesis ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )? otherlv_42= RightParenthesis ) ) | ( () ( (lv_fn_44_0= ruleBuiltInFn ) ) otherlv_45= LeftParenthesis ( ( (lv_args_46_0= ruleExpr ) ) (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )* )? otherlv_49= RightParenthesis ) | ( () ( (otherlv_51= RULE_ID ) ) otherlv_52= LeftParenthesis ( ( (lv_args_53_0= ruleExpr ) ) (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )* )? otherlv_56= RightParenthesis ) | ( () ( (lv_lintStmt_58_0= ruleLintStatement ) ) ) | (otherlv_59= LeftSquareBracket this_Expr_60= ruleExpr ( ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? ) | ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* ) ) otherlv_71= RightSquareBracket ) | (otherlv_72= LeftCurlyBracket this_Expr_73= ruleExpr ( ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? ) | ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* ) ) otherlv_84= RightCurlyBracket ) | ( () otherlv_86= LeftSquareBracket otherlv_87= RightSquareBracket ) | ( () otherlv_89= LeftCurlyBracket otherlv_90= RightCurlyBracket ) | ( () otherlv_92= Let ( (lv_binding_93_0= ruleLetBinding ) ) otherlv_94= Semicolon ( (lv_expr_95_0= ruleExpr ) ) ) | (otherlv_96= LeftParenthesis this_Expr_97= ruleExpr otherlv_98= RightParenthesis ) ) )
            // InternalResoluteParser.g:2635:1: ( ( () ( ( ruleQCREF ) ) ) | ( () otherlv_3= This (otherlv_4= FullStop ( (lv_sub_5_0= ruleNestedDotID ) ) )? ) | ( () otherlv_7= Fail ( ( (lv_val_8_0= ruleExpr ) ) | (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk ) ) ) | ( () ( (lv_val_13_0= ruleIntegerTerm ) ) ) | ( () ( (lv_val_15_0= ruleRealTerm ) ) ) | ( () ( (lv_val_17_0= ruleBooleanLiteral ) ) ) | ( () ( (lv_val_19_0= ruleStringTerm ) ) ) | ( () otherlv_21= If ( (lv_cond_22_0= ruleExpr ) ) otherlv_23= Then ( (lv_then_24_0= ruleExpr ) ) otherlv_25= Else ( (lv_else_26_0= ruleExpr ) ) ) | ( () ( ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) ) ) (otherlv_29= LeftParenthesis ( (lv_args_30_0= ruleArg ) ) otherlv_31= RightParenthesis )+ otherlv_32= FullStop ( (lv_expr_33_0= ruleExpr ) ) ) | ( ( ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis ) )=> ( () ( (lv_libName_35_0= RULE_ID ) ) otherlv_36= FullStop ( (lv_fnName_37_0= RULE_ID ) ) otherlv_38= LeftParenthesis ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )? otherlv_42= RightParenthesis ) ) | ( () ( (lv_fn_44_0= ruleBuiltInFn ) ) otherlv_45= LeftParenthesis ( ( (lv_args_46_0= ruleExpr ) ) (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )* )? otherlv_49= RightParenthesis ) | ( () ( (otherlv_51= RULE_ID ) ) otherlv_52= LeftParenthesis ( ( (lv_args_53_0= ruleExpr ) ) (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )* )? otherlv_56= RightParenthesis ) | ( () ( (lv_lintStmt_58_0= ruleLintStatement ) ) ) | (otherlv_59= LeftSquareBracket this_Expr_60= ruleExpr ( ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? ) | ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* ) ) otherlv_71= RightSquareBracket ) | (otherlv_72= LeftCurlyBracket this_Expr_73= ruleExpr ( ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? ) | ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* ) ) otherlv_84= RightCurlyBracket ) | ( () otherlv_86= LeftSquareBracket otherlv_87= RightSquareBracket ) | ( () otherlv_89= LeftCurlyBracket otherlv_90= RightCurlyBracket ) | ( () otherlv_92= Let ( (lv_binding_93_0= ruleLetBinding ) ) otherlv_94= Semicolon ( (lv_expr_95_0= ruleExpr ) ) ) | (otherlv_96= LeftParenthesis this_Expr_97= ruleExpr otherlv_98= RightParenthesis ) )
            {
            // InternalResoluteParser.g:2635:1: ( ( () ( ( ruleQCREF ) ) ) | ( () otherlv_3= This (otherlv_4= FullStop ( (lv_sub_5_0= ruleNestedDotID ) ) )? ) | ( () otherlv_7= Fail ( ( (lv_val_8_0= ruleExpr ) ) | (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk ) ) ) | ( () ( (lv_val_13_0= ruleIntegerTerm ) ) ) | ( () ( (lv_val_15_0= ruleRealTerm ) ) ) | ( () ( (lv_val_17_0= ruleBooleanLiteral ) ) ) | ( () ( (lv_val_19_0= ruleStringTerm ) ) ) | ( () otherlv_21= If ( (lv_cond_22_0= ruleExpr ) ) otherlv_23= Then ( (lv_then_24_0= ruleExpr ) ) otherlv_25= Else ( (lv_else_26_0= ruleExpr ) ) ) | ( () ( ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) ) ) (otherlv_29= LeftParenthesis ( (lv_args_30_0= ruleArg ) ) otherlv_31= RightParenthesis )+ otherlv_32= FullStop ( (lv_expr_33_0= ruleExpr ) ) ) | ( ( ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis ) )=> ( () ( (lv_libName_35_0= RULE_ID ) ) otherlv_36= FullStop ( (lv_fnName_37_0= RULE_ID ) ) otherlv_38= LeftParenthesis ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )? otherlv_42= RightParenthesis ) ) | ( () ( (lv_fn_44_0= ruleBuiltInFn ) ) otherlv_45= LeftParenthesis ( ( (lv_args_46_0= ruleExpr ) ) (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )* )? otherlv_49= RightParenthesis ) | ( () ( (otherlv_51= RULE_ID ) ) otherlv_52= LeftParenthesis ( ( (lv_args_53_0= ruleExpr ) ) (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )* )? otherlv_56= RightParenthesis ) | ( () ( (lv_lintStmt_58_0= ruleLintStatement ) ) ) | (otherlv_59= LeftSquareBracket this_Expr_60= ruleExpr ( ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? ) | ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* ) ) otherlv_71= RightSquareBracket ) | (otherlv_72= LeftCurlyBracket this_Expr_73= ruleExpr ( ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? ) | ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* ) ) otherlv_84= RightCurlyBracket ) | ( () otherlv_86= LeftSquareBracket otherlv_87= RightSquareBracket ) | ( () otherlv_89= LeftCurlyBracket otherlv_90= RightCurlyBracket ) | ( () otherlv_92= Let ( (lv_binding_93_0= ruleLetBinding ) ) otherlv_94= Semicolon ( (lv_expr_95_0= ruleExpr ) ) ) | (otherlv_96= LeftParenthesis this_Expr_97= ruleExpr otherlv_98= RightParenthesis ) )
            int alt49=19;
            alt49 = dfa49.predict(input);
            switch (alt49) {
                case 1 :
                    // InternalResoluteParser.g:2635:2: ( () ( ( ruleQCREF ) ) )
                    {
                    // InternalResoluteParser.g:2635:2: ( () ( ( ruleQCREF ) ) )
                    // InternalResoluteParser.g:2635:3: () ( ( ruleQCREF ) )
                    {
                    // InternalResoluteParser.g:2635:3: ()
                    // InternalResoluteParser.g:2636:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getIdExprAction_0_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:2641:2: ( ( ruleQCREF ) )
                    // InternalResoluteParser.g:2642:1: ( ruleQCREF )
                    {
                    // InternalResoluteParser.g:2642:1: ( ruleQCREF )
                    // InternalResoluteParser.g:2643:3: ruleQCREF
                    {
                    if ( state.backtracking==0 ) {

                      			if (current==null) {
                      	            current = createModelElement(grammarAccess.getAtomicExprRule());
                      	        }
                              
                    }
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getIdNamedElementCrossReference_0_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    ruleQCREF();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:2658:6: ( () otherlv_3= This (otherlv_4= FullStop ( (lv_sub_5_0= ruleNestedDotID ) ) )? )
                    {
                    // InternalResoluteParser.g:2658:6: ( () otherlv_3= This (otherlv_4= FullStop ( (lv_sub_5_0= ruleNestedDotID ) ) )? )
                    // InternalResoluteParser.g:2658:7: () otherlv_3= This (otherlv_4= FullStop ( (lv_sub_5_0= ruleNestedDotID ) ) )?
                    {
                    // InternalResoluteParser.g:2658:7: ()
                    // InternalResoluteParser.g:2659:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getThisExprAction_1_0(),
                                  current);
                          
                    }

                    }

                    otherlv_3=(Token)match(input,This,FollowSets000.FOLLOW_35); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_3, grammarAccess.getAtomicExprAccess().getThisKeyword_1_1());
                          
                    }
                    // InternalResoluteParser.g:2669:1: (otherlv_4= FullStop ( (lv_sub_5_0= ruleNestedDotID ) ) )?
                    int alt30=2;
                    int LA30_0 = input.LA(1);

                    if ( (LA30_0==FullStop) ) {
                        alt30=1;
                    }
                    switch (alt30) {
                        case 1 :
                            // InternalResoluteParser.g:2670:2: otherlv_4= FullStop ( (lv_sub_5_0= ruleNestedDotID ) )
                            {
                            otherlv_4=(Token)match(input,FullStop,FollowSets000.FOLLOW_10); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                  	newLeafNode(otherlv_4, grammarAccess.getAtomicExprAccess().getFullStopKeyword_1_2_0());
                                  
                            }
                            // InternalResoluteParser.g:2674:1: ( (lv_sub_5_0= ruleNestedDotID ) )
                            // InternalResoluteParser.g:2675:1: (lv_sub_5_0= ruleNestedDotID )
                            {
                            // InternalResoluteParser.g:2675:1: (lv_sub_5_0= ruleNestedDotID )
                            // InternalResoluteParser.g:2676:3: lv_sub_5_0= ruleNestedDotID
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getAtomicExprAccess().getSubNestedDotIDParserRuleCall_1_2_1_0()); 
                              	    
                            }
                            pushFollow(FollowSets000.FOLLOW_2);
                            lv_sub_5_0=ruleNestedDotID();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"sub",
                                      		lv_sub_5_0, 
                                      		"com.rockwellcollins.atc.resolute.Resolute.NestedDotID");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }


                            }
                            break;

                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalResoluteParser.g:2693:6: ( () otherlv_7= Fail ( ( (lv_val_8_0= ruleExpr ) ) | (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk ) ) )
                    {
                    // InternalResoluteParser.g:2693:6: ( () otherlv_7= Fail ( ( (lv_val_8_0= ruleExpr ) ) | (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk ) ) )
                    // InternalResoluteParser.g:2693:7: () otherlv_7= Fail ( ( (lv_val_8_0= ruleExpr ) ) | (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk ) )
                    {
                    // InternalResoluteParser.g:2693:7: ()
                    // InternalResoluteParser.g:2694:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getFailExprAction_2_0(),
                                  current);
                          
                    }

                    }

                    otherlv_7=(Token)match(input,Fail,FollowSets000.FOLLOW_36); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_7, grammarAccess.getAtomicExprAccess().getFailKeyword_2_1());
                          
                    }
                    // InternalResoluteParser.g:2704:1: ( ( (lv_val_8_0= ruleExpr ) ) | (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk ) )
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( ((LA32_0>=Error_state_reachable && LA32_0<=Is_virtual_processor)||(LA32_0>=Flow_specifications && LA32_0<=Is_abstract_feature)||LA32_0==Enumerated_values||(LA32_0>=End_to_end_flows && LA32_0<=Is_bidirectional)||(LA32_0>=Is_thread_group && LA32_0<=Contain_error)||(LA32_0>=Flow_elements && LA32_0<=Is_processor)||LA32_0==Connections||(LA32_0>=Destination && LA32_0<=Upper_bound)||(LA32_0>=Has_member && LA32_0<=Has_parent)||(LA32_0>=Is_of_type && LA32_0<=Is_process)||(LA32_0>=Direction && LA32_0<=Is_thread)||LA32_0==Analysis||(LA32_0>=Features && LA32_0<=Property)||LA32_0==As_list||(LA32_0>=Is_data && LA32_0<=Is_port)||LA32_0==Warning||(LA32_0>=Append && LA32_0<=As_set)||(LA32_0>=Exists && LA32_0<=Member)||(LA32_0>=Parent && LA32_0<=Source)||LA32_0==Debug||(LA32_0>=Error && LA32_0<=False)||LA32_0==Union||(LA32_0>=Fail && LA32_0<=Name)||(LA32_0>=Size && LA32_0<=Tail)||(LA32_0>=This && LA32_0<=Type)||(LA32_0>=Let && LA32_0<=Sum)||LA32_0==If||LA32_0==LeftParenthesis||LA32_0==HyphenMinus||LA32_0==LeftSquareBracket||LA32_0==LeftCurlyBracket||LA32_0==RULE_REAL_LIT||LA32_0==RULE_INTEGER_LIT||(LA32_0>=RULE_STRING && LA32_0<=RULE_ID)) ) {
                        alt32=1;
                    }
                    else if ( (LA32_0==AsteriskAsterisk) ) {
                        alt32=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 32, 0, input);

                        throw nvae;
                    }
                    switch (alt32) {
                        case 1 :
                            // InternalResoluteParser.g:2704:2: ( (lv_val_8_0= ruleExpr ) )
                            {
                            // InternalResoluteParser.g:2704:2: ( (lv_val_8_0= ruleExpr ) )
                            // InternalResoluteParser.g:2705:1: (lv_val_8_0= ruleExpr )
                            {
                            // InternalResoluteParser.g:2705:1: (lv_val_8_0= ruleExpr )
                            // InternalResoluteParser.g:2706:3: lv_val_8_0= ruleExpr
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getAtomicExprAccess().getValExprParserRuleCall_2_2_0_0()); 
                              	    
                            }
                            pushFollow(FollowSets000.FOLLOW_2);
                            lv_val_8_0=ruleExpr();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"val",
                                      		lv_val_8_0, 
                                      		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }


                            }
                            break;
                        case 2 :
                            // InternalResoluteParser.g:2723:6: (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk )
                            {
                            // InternalResoluteParser.g:2723:6: (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk )
                            // InternalResoluteParser.g:2724:2: otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk
                            {
                            otherlv_9=(Token)match(input,AsteriskAsterisk,FollowSets000.FOLLOW_19); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                  	newLeafNode(otherlv_9, grammarAccess.getAtomicExprAccess().getAsteriskAsteriskKeyword_2_2_1_0());
                                  
                            }
                            // InternalResoluteParser.g:2728:1: ( (lv_failmsg_10_0= ruleClaimText ) )+
                            int cnt31=0;
                            loop31:
                            do {
                                int alt31=2;
                                int LA31_0 = input.LA(1);

                                if ( ((LA31_0>=RULE_STRING && LA31_0<=RULE_ID)) ) {
                                    alt31=1;
                                }


                                switch (alt31) {
                            	case 1 :
                            	    // InternalResoluteParser.g:2729:1: (lv_failmsg_10_0= ruleClaimText )
                            	    {
                            	    // InternalResoluteParser.g:2729:1: (lv_failmsg_10_0= ruleClaimText )
                            	    // InternalResoluteParser.g:2730:3: lv_failmsg_10_0= ruleClaimText
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getFailmsgClaimTextParserRuleCall_2_2_1_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FollowSets000.FOLLOW_20);
                            	    lv_failmsg_10_0=ruleClaimText();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"failmsg",
                            	              		lv_failmsg_10_0, 
                            	              		"com.rockwellcollins.atc.resolute.Resolute.ClaimText");
                            	      	        afterParserOrEnumRuleCall();
                            	      	    
                            	    }

                            	    }


                            	    }
                            	    break;

                            	default :
                            	    if ( cnt31 >= 1 ) break loop31;
                            	    if (state.backtracking>0) {state.failed=true; return current;}
                                        EarlyExitException eee =
                                            new EarlyExitException(31, input);
                                        throw eee;
                                }
                                cnt31++;
                            } while (true);

                            otherlv_11=(Token)match(input,AsteriskAsterisk,FollowSets000.FOLLOW_2); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                  	newLeafNode(otherlv_11, grammarAccess.getAtomicExprAccess().getAsteriskAsteriskKeyword_2_2_1_2());
                                  
                            }

                            }


                            }
                            break;

                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalResoluteParser.g:2752:6: ( () ( (lv_val_13_0= ruleIntegerTerm ) ) )
                    {
                    // InternalResoluteParser.g:2752:6: ( () ( (lv_val_13_0= ruleIntegerTerm ) ) )
                    // InternalResoluteParser.g:2752:7: () ( (lv_val_13_0= ruleIntegerTerm ) )
                    {
                    // InternalResoluteParser.g:2752:7: ()
                    // InternalResoluteParser.g:2753:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getIntExprAction_3_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:2758:2: ( (lv_val_13_0= ruleIntegerTerm ) )
                    // InternalResoluteParser.g:2759:1: (lv_val_13_0= ruleIntegerTerm )
                    {
                    // InternalResoluteParser.g:2759:1: (lv_val_13_0= ruleIntegerTerm )
                    // InternalResoluteParser.g:2760:3: lv_val_13_0= ruleIntegerTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getValIntegerTermParserRuleCall_3_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_val_13_0=ruleIntegerTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"val",
                              		lv_val_13_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.IntegerTerm");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 5 :
                    // InternalResoluteParser.g:2777:6: ( () ( (lv_val_15_0= ruleRealTerm ) ) )
                    {
                    // InternalResoluteParser.g:2777:6: ( () ( (lv_val_15_0= ruleRealTerm ) ) )
                    // InternalResoluteParser.g:2777:7: () ( (lv_val_15_0= ruleRealTerm ) )
                    {
                    // InternalResoluteParser.g:2777:7: ()
                    // InternalResoluteParser.g:2778:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getRealExprAction_4_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:2783:2: ( (lv_val_15_0= ruleRealTerm ) )
                    // InternalResoluteParser.g:2784:1: (lv_val_15_0= ruleRealTerm )
                    {
                    // InternalResoluteParser.g:2784:1: (lv_val_15_0= ruleRealTerm )
                    // InternalResoluteParser.g:2785:3: lv_val_15_0= ruleRealTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getValRealTermParserRuleCall_4_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_val_15_0=ruleRealTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"val",
                              		lv_val_15_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.RealTerm");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 6 :
                    // InternalResoluteParser.g:2802:6: ( () ( (lv_val_17_0= ruleBooleanLiteral ) ) )
                    {
                    // InternalResoluteParser.g:2802:6: ( () ( (lv_val_17_0= ruleBooleanLiteral ) ) )
                    // InternalResoluteParser.g:2802:7: () ( (lv_val_17_0= ruleBooleanLiteral ) )
                    {
                    // InternalResoluteParser.g:2802:7: ()
                    // InternalResoluteParser.g:2803:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getBoolExprAction_5_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:2808:2: ( (lv_val_17_0= ruleBooleanLiteral ) )
                    // InternalResoluteParser.g:2809:1: (lv_val_17_0= ruleBooleanLiteral )
                    {
                    // InternalResoluteParser.g:2809:1: (lv_val_17_0= ruleBooleanLiteral )
                    // InternalResoluteParser.g:2810:3: lv_val_17_0= ruleBooleanLiteral
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getValBooleanLiteralParserRuleCall_5_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_val_17_0=ruleBooleanLiteral();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"val",
                              		lv_val_17_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.BooleanLiteral");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 7 :
                    // InternalResoluteParser.g:2827:6: ( () ( (lv_val_19_0= ruleStringTerm ) ) )
                    {
                    // InternalResoluteParser.g:2827:6: ( () ( (lv_val_19_0= ruleStringTerm ) ) )
                    // InternalResoluteParser.g:2827:7: () ( (lv_val_19_0= ruleStringTerm ) )
                    {
                    // InternalResoluteParser.g:2827:7: ()
                    // InternalResoluteParser.g:2828:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getStringExprAction_6_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:2833:2: ( (lv_val_19_0= ruleStringTerm ) )
                    // InternalResoluteParser.g:2834:1: (lv_val_19_0= ruleStringTerm )
                    {
                    // InternalResoluteParser.g:2834:1: (lv_val_19_0= ruleStringTerm )
                    // InternalResoluteParser.g:2835:3: lv_val_19_0= ruleStringTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getValStringTermParserRuleCall_6_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_val_19_0=ruleStringTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"val",
                              		lv_val_19_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.StringTerm");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 8 :
                    // InternalResoluteParser.g:2852:6: ( () otherlv_21= If ( (lv_cond_22_0= ruleExpr ) ) otherlv_23= Then ( (lv_then_24_0= ruleExpr ) ) otherlv_25= Else ( (lv_else_26_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:2852:6: ( () otherlv_21= If ( (lv_cond_22_0= ruleExpr ) ) otherlv_23= Then ( (lv_then_24_0= ruleExpr ) ) otherlv_25= Else ( (lv_else_26_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:2852:7: () otherlv_21= If ( (lv_cond_22_0= ruleExpr ) ) otherlv_23= Then ( (lv_then_24_0= ruleExpr ) ) otherlv_25= Else ( (lv_else_26_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:2852:7: ()
                    // InternalResoluteParser.g:2853:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getIfThenElseExprAction_7_0(),
                                  current);
                          
                    }

                    }

                    otherlv_21=(Token)match(input,If,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_21, grammarAccess.getAtomicExprAccess().getIfKeyword_7_1());
                          
                    }
                    // InternalResoluteParser.g:2863:1: ( (lv_cond_22_0= ruleExpr ) )
                    // InternalResoluteParser.g:2864:1: (lv_cond_22_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:2864:1: (lv_cond_22_0= ruleExpr )
                    // InternalResoluteParser.g:2865:3: lv_cond_22_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getCondExprParserRuleCall_7_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_37);
                    lv_cond_22_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"cond",
                              		lv_cond_22_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_23=(Token)match(input,Then,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_23, grammarAccess.getAtomicExprAccess().getThenKeyword_7_3());
                          
                    }
                    // InternalResoluteParser.g:2886:1: ( (lv_then_24_0= ruleExpr ) )
                    // InternalResoluteParser.g:2887:1: (lv_then_24_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:2887:1: (lv_then_24_0= ruleExpr )
                    // InternalResoluteParser.g:2888:3: lv_then_24_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getThenExprParserRuleCall_7_4_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_38);
                    lv_then_24_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"then",
                              		lv_then_24_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_25=(Token)match(input,Else,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_25, grammarAccess.getAtomicExprAccess().getElseKeyword_7_5());
                          
                    }
                    // InternalResoluteParser.g:2909:1: ( (lv_else_26_0= ruleExpr ) )
                    // InternalResoluteParser.g:2910:1: (lv_else_26_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:2910:1: (lv_else_26_0= ruleExpr )
                    // InternalResoluteParser.g:2911:3: lv_else_26_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getElseExprParserRuleCall_7_6_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_else_26_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"else",
                              		lv_else_26_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 9 :
                    // InternalResoluteParser.g:2928:6: ( () ( ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) ) ) (otherlv_29= LeftParenthesis ( (lv_args_30_0= ruleArg ) ) otherlv_31= RightParenthesis )+ otherlv_32= FullStop ( (lv_expr_33_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:2928:6: ( () ( ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) ) ) (otherlv_29= LeftParenthesis ( (lv_args_30_0= ruleArg ) ) otherlv_31= RightParenthesis )+ otherlv_32= FullStop ( (lv_expr_33_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:2928:7: () ( ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) ) ) (otherlv_29= LeftParenthesis ( (lv_args_30_0= ruleArg ) ) otherlv_31= RightParenthesis )+ otherlv_32= FullStop ( (lv_expr_33_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:2928:7: ()
                    // InternalResoluteParser.g:2929:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getQuantifiedExprAction_8_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:2934:2: ( ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) ) )
                    // InternalResoluteParser.g:2935:1: ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) )
                    {
                    // InternalResoluteParser.g:2935:1: ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) )
                    // InternalResoluteParser.g:2936:1: (lv_quant_28_1= Forall | lv_quant_28_2= Exists )
                    {
                    // InternalResoluteParser.g:2936:1: (lv_quant_28_1= Forall | lv_quant_28_2= Exists )
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0==Forall) ) {
                        alt33=1;
                    }
                    else if ( (LA33_0==Exists) ) {
                        alt33=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 33, 0, input);

                        throw nvae;
                    }
                    switch (alt33) {
                        case 1 :
                            // InternalResoluteParser.g:2937:3: lv_quant_28_1= Forall
                            {
                            lv_quant_28_1=(Token)match(input,Forall,FollowSets000.FOLLOW_14); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_quant_28_1, grammarAccess.getAtomicExprAccess().getQuantForallKeyword_8_1_0_0());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getAtomicExprRule());
                              	        }
                                     		setWithLastConsumed(current, "quant", lv_quant_28_1, null);
                              	    
                            }

                            }
                            break;
                        case 2 :
                            // InternalResoluteParser.g:2950:8: lv_quant_28_2= Exists
                            {
                            lv_quant_28_2=(Token)match(input,Exists,FollowSets000.FOLLOW_14); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_quant_28_2, grammarAccess.getAtomicExprAccess().getQuantExistsKeyword_8_1_0_1());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getAtomicExprRule());
                              	        }
                                     		setWithLastConsumed(current, "quant", lv_quant_28_2, null);
                              	    
                            }

                            }
                            break;

                    }


                    }


                    }

                    // InternalResoluteParser.g:2966:2: (otherlv_29= LeftParenthesis ( (lv_args_30_0= ruleArg ) ) otherlv_31= RightParenthesis )+
                    int cnt34=0;
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( (LA34_0==LeftParenthesis) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // InternalResoluteParser.g:2967:2: otherlv_29= LeftParenthesis ( (lv_args_30_0= ruleArg ) ) otherlv_31= RightParenthesis
                    	    {
                    	    otherlv_29=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	          	newLeafNode(otherlv_29, grammarAccess.getAtomicExprAccess().getLeftParenthesisKeyword_8_2_0());
                    	          
                    	    }
                    	    // InternalResoluteParser.g:2971:1: ( (lv_args_30_0= ruleArg ) )
                    	    // InternalResoluteParser.g:2972:1: (lv_args_30_0= ruleArg )
                    	    {
                    	    // InternalResoluteParser.g:2972:1: (lv_args_30_0= ruleArg )
                    	    // InternalResoluteParser.g:2973:3: lv_args_30_0= ruleArg
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getArgsArgParserRuleCall_8_2_1_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_34);
                    	    lv_args_30_0=ruleArg();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"args",
                    	              		lv_args_30_0, 
                    	              		"com.rockwellcollins.atc.resolute.Resolute.Arg");
                    	      	        afterParserOrEnumRuleCall();
                    	      	    
                    	    }

                    	    }


                    	    }

                    	    otherlv_31=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_39); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	          	newLeafNode(otherlv_31, grammarAccess.getAtomicExprAccess().getRightParenthesisKeyword_8_2_2());
                    	          
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt34 >= 1 ) break loop34;
                    	    if (state.backtracking>0) {state.failed=true; return current;}
                                EarlyExitException eee =
                                    new EarlyExitException(34, input);
                                throw eee;
                        }
                        cnt34++;
                    } while (true);

                    otherlv_32=(Token)match(input,FullStop,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_32, grammarAccess.getAtomicExprAccess().getFullStopKeyword_8_3());
                          
                    }
                    // InternalResoluteParser.g:2999:1: ( (lv_expr_33_0= ruleExpr ) )
                    // InternalResoluteParser.g:3000:1: (lv_expr_33_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:3000:1: (lv_expr_33_0= ruleExpr )
                    // InternalResoluteParser.g:3001:3: lv_expr_33_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getExprExprParserRuleCall_8_4_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_33_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_33_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 10 :
                    // InternalResoluteParser.g:3018:6: ( ( ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis ) )=> ( () ( (lv_libName_35_0= RULE_ID ) ) otherlv_36= FullStop ( (lv_fnName_37_0= RULE_ID ) ) otherlv_38= LeftParenthesis ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )? otherlv_42= RightParenthesis ) )
                    {
                    // InternalResoluteParser.g:3018:6: ( ( ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis ) )=> ( () ( (lv_libName_35_0= RULE_ID ) ) otherlv_36= FullStop ( (lv_fnName_37_0= RULE_ID ) ) otherlv_38= LeftParenthesis ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )? otherlv_42= RightParenthesis ) )
                    // InternalResoluteParser.g:3018:7: ( ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis ) )=> ( () ( (lv_libName_35_0= RULE_ID ) ) otherlv_36= FullStop ( (lv_fnName_37_0= RULE_ID ) ) otherlv_38= LeftParenthesis ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )? otherlv_42= RightParenthesis )
                    {
                    // InternalResoluteParser.g:3045:5: ( () ( (lv_libName_35_0= RULE_ID ) ) otherlv_36= FullStop ( (lv_fnName_37_0= RULE_ID ) ) otherlv_38= LeftParenthesis ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )? otherlv_42= RightParenthesis )
                    // InternalResoluteParser.g:3045:6: () ( (lv_libName_35_0= RULE_ID ) ) otherlv_36= FullStop ( (lv_fnName_37_0= RULE_ID ) ) otherlv_38= LeftParenthesis ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )? otherlv_42= RightParenthesis
                    {
                    // InternalResoluteParser.g:3045:6: ()
                    // InternalResoluteParser.g:3046:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getLibraryFnCallExprAction_9_0_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:3051:2: ( (lv_libName_35_0= RULE_ID ) )
                    // InternalResoluteParser.g:3052:1: (lv_libName_35_0= RULE_ID )
                    {
                    // InternalResoluteParser.g:3052:1: (lv_libName_35_0= RULE_ID )
                    // InternalResoluteParser.g:3053:3: lv_libName_35_0= RULE_ID
                    {
                    lv_libName_35_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_9); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_libName_35_0, grammarAccess.getAtomicExprAccess().getLibNameIDTerminalRuleCall_9_0_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAtomicExprRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"libName",
                              		lv_libName_35_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.ID");
                      	    
                    }

                    }


                    }

                    otherlv_36=(Token)match(input,FullStop,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_36, grammarAccess.getAtomicExprAccess().getFullStopKeyword_9_0_2());
                          
                    }
                    // InternalResoluteParser.g:3074:1: ( (lv_fnName_37_0= RULE_ID ) )
                    // InternalResoluteParser.g:3075:1: (lv_fnName_37_0= RULE_ID )
                    {
                    // InternalResoluteParser.g:3075:1: (lv_fnName_37_0= RULE_ID )
                    // InternalResoluteParser.g:3076:3: lv_fnName_37_0= RULE_ID
                    {
                    lv_fnName_37_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_14); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_fnName_37_0, grammarAccess.getAtomicExprAccess().getFnNameIDTerminalRuleCall_9_0_3_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAtomicExprRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"fnName",
                              		lv_fnName_37_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.ID");
                      	    
                    }

                    }


                    }

                    otherlv_38=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_40); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_38, grammarAccess.getAtomicExprAccess().getLeftParenthesisKeyword_9_0_4());
                          
                    }
                    // InternalResoluteParser.g:3097:1: ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);

                    if ( ((LA36_0>=Error_state_reachable && LA36_0<=Is_virtual_processor)||(LA36_0>=Flow_specifications && LA36_0<=Is_abstract_feature)||LA36_0==Enumerated_values||(LA36_0>=End_to_end_flows && LA36_0<=Is_bidirectional)||(LA36_0>=Is_thread_group && LA36_0<=Contain_error)||(LA36_0>=Flow_elements && LA36_0<=Is_processor)||LA36_0==Connections||(LA36_0>=Destination && LA36_0<=Upper_bound)||(LA36_0>=Has_member && LA36_0<=Has_parent)||(LA36_0>=Is_of_type && LA36_0<=Is_process)||(LA36_0>=Direction && LA36_0<=Is_thread)||LA36_0==Analysis||(LA36_0>=Features && LA36_0<=Property)||LA36_0==As_list||(LA36_0>=Is_data && LA36_0<=Is_port)||LA36_0==Warning||(LA36_0>=Append && LA36_0<=As_set)||(LA36_0>=Exists && LA36_0<=Member)||(LA36_0>=Parent && LA36_0<=Source)||LA36_0==Debug||(LA36_0>=Error && LA36_0<=False)||LA36_0==Union||(LA36_0>=Fail && LA36_0<=Name)||(LA36_0>=Size && LA36_0<=Tail)||(LA36_0>=This && LA36_0<=Type)||(LA36_0>=Let && LA36_0<=Sum)||LA36_0==If||LA36_0==LeftParenthesis||LA36_0==HyphenMinus||LA36_0==LeftSquareBracket||LA36_0==LeftCurlyBracket||LA36_0==RULE_REAL_LIT||LA36_0==RULE_INTEGER_LIT||(LA36_0>=RULE_STRING && LA36_0<=RULE_ID)) ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // InternalResoluteParser.g:3097:2: ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )*
                            {
                            // InternalResoluteParser.g:3097:2: ( (lv_args_39_0= ruleExpr ) )
                            // InternalResoluteParser.g:3098:1: (lv_args_39_0= ruleExpr )
                            {
                            // InternalResoluteParser.g:3098:1: (lv_args_39_0= ruleExpr )
                            // InternalResoluteParser.g:3099:3: lv_args_39_0= ruleExpr
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getAtomicExprAccess().getArgsExprParserRuleCall_9_0_5_0_0()); 
                              	    
                            }
                            pushFollow(FollowSets000.FOLLOW_16);
                            lv_args_39_0=ruleExpr();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                              	        }
                                     		add(
                                     			current, 
                                     			"args",
                                      		lv_args_39_0, 
                                      		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }

                            // InternalResoluteParser.g:3115:2: (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )*
                            loop35:
                            do {
                                int alt35=2;
                                int LA35_0 = input.LA(1);

                                if ( (LA35_0==Comma) ) {
                                    alt35=1;
                                }


                                switch (alt35) {
                            	case 1 :
                            	    // InternalResoluteParser.g:3116:2: otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) )
                            	    {
                            	    otherlv_40=(Token)match(input,Comma,FollowSets000.FOLLOW_12); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_40, grammarAccess.getAtomicExprAccess().getCommaKeyword_9_0_5_1_0());
                            	          
                            	    }
                            	    // InternalResoluteParser.g:3120:1: ( (lv_args_41_0= ruleExpr ) )
                            	    // InternalResoluteParser.g:3121:1: (lv_args_41_0= ruleExpr )
                            	    {
                            	    // InternalResoluteParser.g:3121:1: (lv_args_41_0= ruleExpr )
                            	    // InternalResoluteParser.g:3122:3: lv_args_41_0= ruleExpr
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getArgsExprParserRuleCall_9_0_5_1_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FollowSets000.FOLLOW_16);
                            	    lv_args_41_0=ruleExpr();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"args",
                            	              		lv_args_41_0, 
                            	              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                            	      	        afterParserOrEnumRuleCall();
                            	      	    
                            	    }

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop35;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_42=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_42, grammarAccess.getAtomicExprAccess().getRightParenthesisKeyword_9_0_6());
                          
                    }

                    }


                    }


                    }
                    break;
                case 11 :
                    // InternalResoluteParser.g:3144:6: ( () ( (lv_fn_44_0= ruleBuiltInFn ) ) otherlv_45= LeftParenthesis ( ( (lv_args_46_0= ruleExpr ) ) (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )* )? otherlv_49= RightParenthesis )
                    {
                    // InternalResoluteParser.g:3144:6: ( () ( (lv_fn_44_0= ruleBuiltInFn ) ) otherlv_45= LeftParenthesis ( ( (lv_args_46_0= ruleExpr ) ) (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )* )? otherlv_49= RightParenthesis )
                    // InternalResoluteParser.g:3144:7: () ( (lv_fn_44_0= ruleBuiltInFn ) ) otherlv_45= LeftParenthesis ( ( (lv_args_46_0= ruleExpr ) ) (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )* )? otherlv_49= RightParenthesis
                    {
                    // InternalResoluteParser.g:3144:7: ()
                    // InternalResoluteParser.g:3145:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getBuiltInFnCallExprAction_10_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:3150:2: ( (lv_fn_44_0= ruleBuiltInFn ) )
                    // InternalResoluteParser.g:3151:1: (lv_fn_44_0= ruleBuiltInFn )
                    {
                    // InternalResoluteParser.g:3151:1: (lv_fn_44_0= ruleBuiltInFn )
                    // InternalResoluteParser.g:3152:3: lv_fn_44_0= ruleBuiltInFn
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getFnBuiltInFnParserRuleCall_10_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_14);
                    lv_fn_44_0=ruleBuiltInFn();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"fn",
                              		lv_fn_44_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.BuiltInFn");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_45=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_40); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_45, grammarAccess.getAtomicExprAccess().getLeftParenthesisKeyword_10_2());
                          
                    }
                    // InternalResoluteParser.g:3173:1: ( ( (lv_args_46_0= ruleExpr ) ) (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )* )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( ((LA38_0>=Error_state_reachable && LA38_0<=Is_virtual_processor)||(LA38_0>=Flow_specifications && LA38_0<=Is_abstract_feature)||LA38_0==Enumerated_values||(LA38_0>=End_to_end_flows && LA38_0<=Is_bidirectional)||(LA38_0>=Is_thread_group && LA38_0<=Contain_error)||(LA38_0>=Flow_elements && LA38_0<=Is_processor)||LA38_0==Connections||(LA38_0>=Destination && LA38_0<=Upper_bound)||(LA38_0>=Has_member && LA38_0<=Has_parent)||(LA38_0>=Is_of_type && LA38_0<=Is_process)||(LA38_0>=Direction && LA38_0<=Is_thread)||LA38_0==Analysis||(LA38_0>=Features && LA38_0<=Property)||LA38_0==As_list||(LA38_0>=Is_data && LA38_0<=Is_port)||LA38_0==Warning||(LA38_0>=Append && LA38_0<=As_set)||(LA38_0>=Exists && LA38_0<=Member)||(LA38_0>=Parent && LA38_0<=Source)||LA38_0==Debug||(LA38_0>=Error && LA38_0<=False)||LA38_0==Union||(LA38_0>=Fail && LA38_0<=Name)||(LA38_0>=Size && LA38_0<=Tail)||(LA38_0>=This && LA38_0<=Type)||(LA38_0>=Let && LA38_0<=Sum)||LA38_0==If||LA38_0==LeftParenthesis||LA38_0==HyphenMinus||LA38_0==LeftSquareBracket||LA38_0==LeftCurlyBracket||LA38_0==RULE_REAL_LIT||LA38_0==RULE_INTEGER_LIT||(LA38_0>=RULE_STRING && LA38_0<=RULE_ID)) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // InternalResoluteParser.g:3173:2: ( (lv_args_46_0= ruleExpr ) ) (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )*
                            {
                            // InternalResoluteParser.g:3173:2: ( (lv_args_46_0= ruleExpr ) )
                            // InternalResoluteParser.g:3174:1: (lv_args_46_0= ruleExpr )
                            {
                            // InternalResoluteParser.g:3174:1: (lv_args_46_0= ruleExpr )
                            // InternalResoluteParser.g:3175:3: lv_args_46_0= ruleExpr
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getAtomicExprAccess().getArgsExprParserRuleCall_10_3_0_0()); 
                              	    
                            }
                            pushFollow(FollowSets000.FOLLOW_16);
                            lv_args_46_0=ruleExpr();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                              	        }
                                     		add(
                                     			current, 
                                     			"args",
                                      		lv_args_46_0, 
                                      		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }

                            // InternalResoluteParser.g:3191:2: (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )*
                            loop37:
                            do {
                                int alt37=2;
                                int LA37_0 = input.LA(1);

                                if ( (LA37_0==Comma) ) {
                                    alt37=1;
                                }


                                switch (alt37) {
                            	case 1 :
                            	    // InternalResoluteParser.g:3192:2: otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) )
                            	    {
                            	    otherlv_47=(Token)match(input,Comma,FollowSets000.FOLLOW_12); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_47, grammarAccess.getAtomicExprAccess().getCommaKeyword_10_3_1_0());
                            	          
                            	    }
                            	    // InternalResoluteParser.g:3196:1: ( (lv_args_48_0= ruleExpr ) )
                            	    // InternalResoluteParser.g:3197:1: (lv_args_48_0= ruleExpr )
                            	    {
                            	    // InternalResoluteParser.g:3197:1: (lv_args_48_0= ruleExpr )
                            	    // InternalResoluteParser.g:3198:3: lv_args_48_0= ruleExpr
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getArgsExprParserRuleCall_10_3_1_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FollowSets000.FOLLOW_16);
                            	    lv_args_48_0=ruleExpr();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"args",
                            	              		lv_args_48_0, 
                            	              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                            	      	        afterParserOrEnumRuleCall();
                            	      	    
                            	    }

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop37;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_49=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_49, grammarAccess.getAtomicExprAccess().getRightParenthesisKeyword_10_4());
                          
                    }

                    }


                    }
                    break;
                case 12 :
                    // InternalResoluteParser.g:3220:6: ( () ( (otherlv_51= RULE_ID ) ) otherlv_52= LeftParenthesis ( ( (lv_args_53_0= ruleExpr ) ) (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )* )? otherlv_56= RightParenthesis )
                    {
                    // InternalResoluteParser.g:3220:6: ( () ( (otherlv_51= RULE_ID ) ) otherlv_52= LeftParenthesis ( ( (lv_args_53_0= ruleExpr ) ) (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )* )? otherlv_56= RightParenthesis )
                    // InternalResoluteParser.g:3220:7: () ( (otherlv_51= RULE_ID ) ) otherlv_52= LeftParenthesis ( ( (lv_args_53_0= ruleExpr ) ) (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )* )? otherlv_56= RightParenthesis
                    {
                    // InternalResoluteParser.g:3220:7: ()
                    // InternalResoluteParser.g:3221:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getFnCallExprAction_11_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:3226:2: ( (otherlv_51= RULE_ID ) )
                    // InternalResoluteParser.g:3227:1: (otherlv_51= RULE_ID )
                    {
                    // InternalResoluteParser.g:3227:1: (otherlv_51= RULE_ID )
                    // InternalResoluteParser.g:3228:3: otherlv_51= RULE_ID
                    {
                    if ( state.backtracking==0 ) {

                      			if (current==null) {
                      	            current = createModelElement(grammarAccess.getAtomicExprRule());
                      	        }
                              
                    }
                    otherlv_51=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_14); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		newLeafNode(otherlv_51, grammarAccess.getAtomicExprAccess().getFnFunctionDefinitionCrossReference_11_1_0()); 
                      	
                    }

                    }


                    }

                    otherlv_52=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_40); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_52, grammarAccess.getAtomicExprAccess().getLeftParenthesisKeyword_11_2());
                          
                    }
                    // InternalResoluteParser.g:3244:1: ( ( (lv_args_53_0= ruleExpr ) ) (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )* )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( ((LA40_0>=Error_state_reachable && LA40_0<=Is_virtual_processor)||(LA40_0>=Flow_specifications && LA40_0<=Is_abstract_feature)||LA40_0==Enumerated_values||(LA40_0>=End_to_end_flows && LA40_0<=Is_bidirectional)||(LA40_0>=Is_thread_group && LA40_0<=Contain_error)||(LA40_0>=Flow_elements && LA40_0<=Is_processor)||LA40_0==Connections||(LA40_0>=Destination && LA40_0<=Upper_bound)||(LA40_0>=Has_member && LA40_0<=Has_parent)||(LA40_0>=Is_of_type && LA40_0<=Is_process)||(LA40_0>=Direction && LA40_0<=Is_thread)||LA40_0==Analysis||(LA40_0>=Features && LA40_0<=Property)||LA40_0==As_list||(LA40_0>=Is_data && LA40_0<=Is_port)||LA40_0==Warning||(LA40_0>=Append && LA40_0<=As_set)||(LA40_0>=Exists && LA40_0<=Member)||(LA40_0>=Parent && LA40_0<=Source)||LA40_0==Debug||(LA40_0>=Error && LA40_0<=False)||LA40_0==Union||(LA40_0>=Fail && LA40_0<=Name)||(LA40_0>=Size && LA40_0<=Tail)||(LA40_0>=This && LA40_0<=Type)||(LA40_0>=Let && LA40_0<=Sum)||LA40_0==If||LA40_0==LeftParenthesis||LA40_0==HyphenMinus||LA40_0==LeftSquareBracket||LA40_0==LeftCurlyBracket||LA40_0==RULE_REAL_LIT||LA40_0==RULE_INTEGER_LIT||(LA40_0>=RULE_STRING && LA40_0<=RULE_ID)) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // InternalResoluteParser.g:3244:2: ( (lv_args_53_0= ruleExpr ) ) (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )*
                            {
                            // InternalResoluteParser.g:3244:2: ( (lv_args_53_0= ruleExpr ) )
                            // InternalResoluteParser.g:3245:1: (lv_args_53_0= ruleExpr )
                            {
                            // InternalResoluteParser.g:3245:1: (lv_args_53_0= ruleExpr )
                            // InternalResoluteParser.g:3246:3: lv_args_53_0= ruleExpr
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getAtomicExprAccess().getArgsExprParserRuleCall_11_3_0_0()); 
                              	    
                            }
                            pushFollow(FollowSets000.FOLLOW_16);
                            lv_args_53_0=ruleExpr();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                              	        }
                                     		add(
                                     			current, 
                                     			"args",
                                      		lv_args_53_0, 
                                      		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }

                            // InternalResoluteParser.g:3262:2: (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )*
                            loop39:
                            do {
                                int alt39=2;
                                int LA39_0 = input.LA(1);

                                if ( (LA39_0==Comma) ) {
                                    alt39=1;
                                }


                                switch (alt39) {
                            	case 1 :
                            	    // InternalResoluteParser.g:3263:2: otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) )
                            	    {
                            	    otherlv_54=(Token)match(input,Comma,FollowSets000.FOLLOW_12); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_54, grammarAccess.getAtomicExprAccess().getCommaKeyword_11_3_1_0());
                            	          
                            	    }
                            	    // InternalResoluteParser.g:3267:1: ( (lv_args_55_0= ruleExpr ) )
                            	    // InternalResoluteParser.g:3268:1: (lv_args_55_0= ruleExpr )
                            	    {
                            	    // InternalResoluteParser.g:3268:1: (lv_args_55_0= ruleExpr )
                            	    // InternalResoluteParser.g:3269:3: lv_args_55_0= ruleExpr
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getArgsExprParserRuleCall_11_3_1_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FollowSets000.FOLLOW_16);
                            	    lv_args_55_0=ruleExpr();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"args",
                            	              		lv_args_55_0, 
                            	              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                            	      	        afterParserOrEnumRuleCall();
                            	      	    
                            	    }

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop39;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_56=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_56, grammarAccess.getAtomicExprAccess().getRightParenthesisKeyword_11_4());
                          
                    }

                    }


                    }
                    break;
                case 13 :
                    // InternalResoluteParser.g:3291:6: ( () ( (lv_lintStmt_58_0= ruleLintStatement ) ) )
                    {
                    // InternalResoluteParser.g:3291:6: ( () ( (lv_lintStmt_58_0= ruleLintStatement ) ) )
                    // InternalResoluteParser.g:3291:7: () ( (lv_lintStmt_58_0= ruleLintStatement ) )
                    {
                    // InternalResoluteParser.g:3291:7: ()
                    // InternalResoluteParser.g:3292:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getLintExprAction_12_0(),
                                  current);
                          
                    }

                    }

                    // InternalResoluteParser.g:3297:2: ( (lv_lintStmt_58_0= ruleLintStatement ) )
                    // InternalResoluteParser.g:3298:1: (lv_lintStmt_58_0= ruleLintStatement )
                    {
                    // InternalResoluteParser.g:3298:1: (lv_lintStmt_58_0= ruleLintStatement )
                    // InternalResoluteParser.g:3299:3: lv_lintStmt_58_0= ruleLintStatement
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getLintStmtLintStatementParserRuleCall_12_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_lintStmt_58_0=ruleLintStatement();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"lintStmt",
                              		lv_lintStmt_58_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.LintStatement");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 14 :
                    // InternalResoluteParser.g:3316:6: (otherlv_59= LeftSquareBracket this_Expr_60= ruleExpr ( ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? ) | ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* ) ) otherlv_71= RightSquareBracket )
                    {
                    // InternalResoluteParser.g:3316:6: (otherlv_59= LeftSquareBracket this_Expr_60= ruleExpr ( ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? ) | ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* ) ) otherlv_71= RightSquareBracket )
                    // InternalResoluteParser.g:3317:2: otherlv_59= LeftSquareBracket this_Expr_60= ruleExpr ( ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? ) | ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* ) ) otherlv_71= RightSquareBracket
                    {
                    otherlv_59=(Token)match(input,LeftSquareBracket,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_59, grammarAccess.getAtomicExprAccess().getLeftSquareBracketKeyword_13_0());
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAtomicExprAccess().getExprParserRuleCall_13_1()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_41);
                    this_Expr_60=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_Expr_60;
                              afterParserOrEnumRuleCall();
                          
                    }
                    // InternalResoluteParser.g:3330:1: ( ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? ) | ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* ) )
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==For) ) {
                        alt44=1;
                    }
                    else if ( (LA44_0==Comma||LA44_0==RightSquareBracket) ) {
                        alt44=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 44, 0, input);

                        throw nvae;
                    }
                    switch (alt44) {
                        case 1 :
                            // InternalResoluteParser.g:3330:2: ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? )
                            {
                            // InternalResoluteParser.g:3330:2: ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? )
                            // InternalResoluteParser.g:3330:3: () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )?
                            {
                            // InternalResoluteParser.g:3330:3: ()
                            // InternalResoluteParser.g:3331:5: 
                            {
                            if ( state.backtracking==0 ) {

                                      current = forceCreateModelElementAndSet(
                                          grammarAccess.getAtomicExprAccess().getListFilterMapExprMapAction_13_2_0_0(),
                                          current);
                                  
                            }

                            }

                            otherlv_62=(Token)match(input,For,FollowSets000.FOLLOW_14); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                  	newLeafNode(otherlv_62, grammarAccess.getAtomicExprAccess().getForKeyword_13_2_0_1());
                                  
                            }
                            // InternalResoluteParser.g:3341:1: (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+
                            int cnt41=0;
                            loop41:
                            do {
                                int alt41=2;
                                int LA41_0 = input.LA(1);

                                if ( (LA41_0==LeftParenthesis) ) {
                                    alt41=1;
                                }


                                switch (alt41) {
                            	case 1 :
                            	    // InternalResoluteParser.g:3342:2: otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis
                            	    {
                            	    otherlv_63=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_10); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_63, grammarAccess.getAtomicExprAccess().getLeftParenthesisKeyword_13_2_0_2_0());
                            	          
                            	    }
                            	    // InternalResoluteParser.g:3346:1: ( (lv_args_64_0= ruleArg ) )
                            	    // InternalResoluteParser.g:3347:1: (lv_args_64_0= ruleArg )
                            	    {
                            	    // InternalResoluteParser.g:3347:1: (lv_args_64_0= ruleArg )
                            	    // InternalResoluteParser.g:3348:3: lv_args_64_0= ruleArg
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getArgsArgParserRuleCall_13_2_0_2_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FollowSets000.FOLLOW_34);
                            	    lv_args_64_0=ruleArg();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"args",
                            	              		lv_args_64_0, 
                            	              		"com.rockwellcollins.atc.resolute.Resolute.Arg");
                            	      	        afterParserOrEnumRuleCall();
                            	      	    
                            	    }

                            	    }


                            	    }

                            	    otherlv_65=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_42); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_65, grammarAccess.getAtomicExprAccess().getRightParenthesisKeyword_13_2_0_2_2());
                            	          
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt41 >= 1 ) break loop41;
                            	    if (state.backtracking>0) {state.failed=true; return current;}
                                        EarlyExitException eee =
                                            new EarlyExitException(41, input);
                                        throw eee;
                                }
                                cnt41++;
                            } while (true);

                            // InternalResoluteParser.g:3369:3: (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )?
                            int alt42=2;
                            int LA42_0 = input.LA(1);

                            if ( (LA42_0==VerticalLine) ) {
                                alt42=1;
                            }
                            switch (alt42) {
                                case 1 :
                                    // InternalResoluteParser.g:3370:2: otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) )
                                    {
                                    otherlv_66=(Token)match(input,VerticalLine,FollowSets000.FOLLOW_12); if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                          	newLeafNode(otherlv_66, grammarAccess.getAtomicExprAccess().getVerticalLineKeyword_13_2_0_3_0());
                                          
                                    }
                                    // InternalResoluteParser.g:3374:1: ( (lv_filter_67_0= ruleExpr ) )
                                    // InternalResoluteParser.g:3375:1: (lv_filter_67_0= ruleExpr )
                                    {
                                    // InternalResoluteParser.g:3375:1: (lv_filter_67_0= ruleExpr )
                                    // InternalResoluteParser.g:3376:3: lv_filter_67_0= ruleExpr
                                    {
                                    if ( state.backtracking==0 ) {
                                       
                                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getFilterExprParserRuleCall_13_2_0_3_1_0()); 
                                      	    
                                    }
                                    pushFollow(FollowSets000.FOLLOW_5);
                                    lv_filter_67_0=ruleExpr();

                                    state._fsp--;
                                    if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                      	        if (current==null) {
                                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                                      	        }
                                             		set(
                                             			current, 
                                             			"filter",
                                              		lv_filter_67_0, 
                                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                                      	        afterParserOrEnumRuleCall();
                                      	    
                                    }

                                    }


                                    }


                                    }
                                    break;

                            }


                            }


                            }
                            break;
                        case 2 :
                            // InternalResoluteParser.g:3393:6: ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* )
                            {
                            // InternalResoluteParser.g:3393:6: ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* )
                            // InternalResoluteParser.g:3393:7: () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )*
                            {
                            // InternalResoluteParser.g:3393:7: ()
                            // InternalResoluteParser.g:3394:5: 
                            {
                            if ( state.backtracking==0 ) {

                                      current = forceCreateModelElementAndAdd(
                                          grammarAccess.getAtomicExprAccess().getListExprExprsAction_13_2_1_0(),
                                          current);
                                  
                            }

                            }

                            // InternalResoluteParser.g:3399:2: (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )*
                            loop43:
                            do {
                                int alt43=2;
                                int LA43_0 = input.LA(1);

                                if ( (LA43_0==Comma) ) {
                                    alt43=1;
                                }


                                switch (alt43) {
                            	case 1 :
                            	    // InternalResoluteParser.g:3400:2: otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) )
                            	    {
                            	    otherlv_69=(Token)match(input,Comma,FollowSets000.FOLLOW_12); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_69, grammarAccess.getAtomicExprAccess().getCommaKeyword_13_2_1_1_0());
                            	          
                            	    }
                            	    // InternalResoluteParser.g:3404:1: ( (lv_exprs_70_0= ruleExpr ) )
                            	    // InternalResoluteParser.g:3405:1: (lv_exprs_70_0= ruleExpr )
                            	    {
                            	    // InternalResoluteParser.g:3405:1: (lv_exprs_70_0= ruleExpr )
                            	    // InternalResoluteParser.g:3406:3: lv_exprs_70_0= ruleExpr
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getExprsExprParserRuleCall_13_2_1_1_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FollowSets000.FOLLOW_43);
                            	    lv_exprs_70_0=ruleExpr();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"exprs",
                            	              		lv_exprs_70_0, 
                            	              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                            	      	        afterParserOrEnumRuleCall();
                            	      	    
                            	    }

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop43;
                                }
                            } while (true);


                            }


                            }
                            break;

                    }

                    otherlv_71=(Token)match(input,RightSquareBracket,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_71, grammarAccess.getAtomicExprAccess().getRightSquareBracketKeyword_13_3());
                          
                    }

                    }


                    }
                    break;
                case 15 :
                    // InternalResoluteParser.g:3428:6: (otherlv_72= LeftCurlyBracket this_Expr_73= ruleExpr ( ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? ) | ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* ) ) otherlv_84= RightCurlyBracket )
                    {
                    // InternalResoluteParser.g:3428:6: (otherlv_72= LeftCurlyBracket this_Expr_73= ruleExpr ( ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? ) | ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* ) ) otherlv_84= RightCurlyBracket )
                    // InternalResoluteParser.g:3429:2: otherlv_72= LeftCurlyBracket this_Expr_73= ruleExpr ( ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? ) | ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* ) ) otherlv_84= RightCurlyBracket
                    {
                    otherlv_72=(Token)match(input,LeftCurlyBracket,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_72, grammarAccess.getAtomicExprAccess().getLeftCurlyBracketKeyword_14_0());
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAtomicExprAccess().getExprParserRuleCall_14_1()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_44);
                    this_Expr_73=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_Expr_73;
                              afterParserOrEnumRuleCall();
                          
                    }
                    // InternalResoluteParser.g:3442:1: ( ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? ) | ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* ) )
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==For) ) {
                        alt48=1;
                    }
                    else if ( (LA48_0==Comma||LA48_0==RightCurlyBracket) ) {
                        alt48=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 48, 0, input);

                        throw nvae;
                    }
                    switch (alt48) {
                        case 1 :
                            // InternalResoluteParser.g:3442:2: ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? )
                            {
                            // InternalResoluteParser.g:3442:2: ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? )
                            // InternalResoluteParser.g:3442:3: () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )?
                            {
                            // InternalResoluteParser.g:3442:3: ()
                            // InternalResoluteParser.g:3443:5: 
                            {
                            if ( state.backtracking==0 ) {

                                      current = forceCreateModelElementAndSet(
                                          grammarAccess.getAtomicExprAccess().getSetFilterMapExprMapAction_14_2_0_0(),
                                          current);
                                  
                            }

                            }

                            otherlv_75=(Token)match(input,For,FollowSets000.FOLLOW_14); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                  	newLeafNode(otherlv_75, grammarAccess.getAtomicExprAccess().getForKeyword_14_2_0_1());
                                  
                            }
                            // InternalResoluteParser.g:3453:1: (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+
                            int cnt45=0;
                            loop45:
                            do {
                                int alt45=2;
                                int LA45_0 = input.LA(1);

                                if ( (LA45_0==LeftParenthesis) ) {
                                    alt45=1;
                                }


                                switch (alt45) {
                            	case 1 :
                            	    // InternalResoluteParser.g:3454:2: otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis
                            	    {
                            	    otherlv_76=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_10); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_76, grammarAccess.getAtomicExprAccess().getLeftParenthesisKeyword_14_2_0_2_0());
                            	          
                            	    }
                            	    // InternalResoluteParser.g:3458:1: ( (lv_args_77_0= ruleArg ) )
                            	    // InternalResoluteParser.g:3459:1: (lv_args_77_0= ruleArg )
                            	    {
                            	    // InternalResoluteParser.g:3459:1: (lv_args_77_0= ruleArg )
                            	    // InternalResoluteParser.g:3460:3: lv_args_77_0= ruleArg
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getArgsArgParserRuleCall_14_2_0_2_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FollowSets000.FOLLOW_34);
                            	    lv_args_77_0=ruleArg();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"args",
                            	              		lv_args_77_0, 
                            	              		"com.rockwellcollins.atc.resolute.Resolute.Arg");
                            	      	        afterParserOrEnumRuleCall();
                            	      	    
                            	    }

                            	    }


                            	    }

                            	    otherlv_78=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_45); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_78, grammarAccess.getAtomicExprAccess().getRightParenthesisKeyword_14_2_0_2_2());
                            	          
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt45 >= 1 ) break loop45;
                            	    if (state.backtracking>0) {state.failed=true; return current;}
                                        EarlyExitException eee =
                                            new EarlyExitException(45, input);
                                        throw eee;
                                }
                                cnt45++;
                            } while (true);

                            // InternalResoluteParser.g:3481:3: (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )?
                            int alt46=2;
                            int LA46_0 = input.LA(1);

                            if ( (LA46_0==VerticalLine) ) {
                                alt46=1;
                            }
                            switch (alt46) {
                                case 1 :
                                    // InternalResoluteParser.g:3482:2: otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) )
                                    {
                                    otherlv_79=(Token)match(input,VerticalLine,FollowSets000.FOLLOW_12); if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                          	newLeafNode(otherlv_79, grammarAccess.getAtomicExprAccess().getVerticalLineKeyword_14_2_0_3_0());
                                          
                                    }
                                    // InternalResoluteParser.g:3486:1: ( (lv_filter_80_0= ruleExpr ) )
                                    // InternalResoluteParser.g:3487:1: (lv_filter_80_0= ruleExpr )
                                    {
                                    // InternalResoluteParser.g:3487:1: (lv_filter_80_0= ruleExpr )
                                    // InternalResoluteParser.g:3488:3: lv_filter_80_0= ruleExpr
                                    {
                                    if ( state.backtracking==0 ) {
                                       
                                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getFilterExprParserRuleCall_14_2_0_3_1_0()); 
                                      	    
                                    }
                                    pushFollow(FollowSets000.FOLLOW_6);
                                    lv_filter_80_0=ruleExpr();

                                    state._fsp--;
                                    if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                      	        if (current==null) {
                                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                                      	        }
                                             		set(
                                             			current, 
                                             			"filter",
                                              		lv_filter_80_0, 
                                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                                      	        afterParserOrEnumRuleCall();
                                      	    
                                    }

                                    }


                                    }


                                    }
                                    break;

                            }


                            }


                            }
                            break;
                        case 2 :
                            // InternalResoluteParser.g:3505:6: ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* )
                            {
                            // InternalResoluteParser.g:3505:6: ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* )
                            // InternalResoluteParser.g:3505:7: () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )*
                            {
                            // InternalResoluteParser.g:3505:7: ()
                            // InternalResoluteParser.g:3506:5: 
                            {
                            if ( state.backtracking==0 ) {

                                      current = forceCreateModelElementAndAdd(
                                          grammarAccess.getAtomicExprAccess().getSetExprExprsAction_14_2_1_0(),
                                          current);
                                  
                            }

                            }

                            // InternalResoluteParser.g:3511:2: (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )*
                            loop47:
                            do {
                                int alt47=2;
                                int LA47_0 = input.LA(1);

                                if ( (LA47_0==Comma) ) {
                                    alt47=1;
                                }


                                switch (alt47) {
                            	case 1 :
                            	    // InternalResoluteParser.g:3512:2: otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) )
                            	    {
                            	    otherlv_82=(Token)match(input,Comma,FollowSets000.FOLLOW_12); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_82, grammarAccess.getAtomicExprAccess().getCommaKeyword_14_2_1_1_0());
                            	          
                            	    }
                            	    // InternalResoluteParser.g:3516:1: ( (lv_exprs_83_0= ruleExpr ) )
                            	    // InternalResoluteParser.g:3517:1: (lv_exprs_83_0= ruleExpr )
                            	    {
                            	    // InternalResoluteParser.g:3517:1: (lv_exprs_83_0= ruleExpr )
                            	    // InternalResoluteParser.g:3518:3: lv_exprs_83_0= ruleExpr
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getExprsExprParserRuleCall_14_2_1_1_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FollowSets000.FOLLOW_46);
                            	    lv_exprs_83_0=ruleExpr();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"exprs",
                            	              		lv_exprs_83_0, 
                            	              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                            	      	        afterParserOrEnumRuleCall();
                            	      	    
                            	    }

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop47;
                                }
                            } while (true);


                            }


                            }
                            break;

                    }

                    otherlv_84=(Token)match(input,RightCurlyBracket,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_84, grammarAccess.getAtomicExprAccess().getRightCurlyBracketKeyword_14_3());
                          
                    }

                    }


                    }
                    break;
                case 16 :
                    // InternalResoluteParser.g:3540:6: ( () otherlv_86= LeftSquareBracket otherlv_87= RightSquareBracket )
                    {
                    // InternalResoluteParser.g:3540:6: ( () otherlv_86= LeftSquareBracket otherlv_87= RightSquareBracket )
                    // InternalResoluteParser.g:3540:7: () otherlv_86= LeftSquareBracket otherlv_87= RightSquareBracket
                    {
                    // InternalResoluteParser.g:3540:7: ()
                    // InternalResoluteParser.g:3541:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getListExprAction_15_0(),
                                  current);
                          
                    }

                    }

                    otherlv_86=(Token)match(input,LeftSquareBracket,FollowSets000.FOLLOW_5); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_86, grammarAccess.getAtomicExprAccess().getLeftSquareBracketKeyword_15_1());
                          
                    }
                    otherlv_87=(Token)match(input,RightSquareBracket,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_87, grammarAccess.getAtomicExprAccess().getRightSquareBracketKeyword_15_2());
                          
                    }

                    }


                    }
                    break;
                case 17 :
                    // InternalResoluteParser.g:3557:6: ( () otherlv_89= LeftCurlyBracket otherlv_90= RightCurlyBracket )
                    {
                    // InternalResoluteParser.g:3557:6: ( () otherlv_89= LeftCurlyBracket otherlv_90= RightCurlyBracket )
                    // InternalResoluteParser.g:3557:7: () otherlv_89= LeftCurlyBracket otherlv_90= RightCurlyBracket
                    {
                    // InternalResoluteParser.g:3557:7: ()
                    // InternalResoluteParser.g:3558:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getSetExprAction_16_0(),
                                  current);
                          
                    }

                    }

                    otherlv_89=(Token)match(input,LeftCurlyBracket,FollowSets000.FOLLOW_6); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_89, grammarAccess.getAtomicExprAccess().getLeftCurlyBracketKeyword_16_1());
                          
                    }
                    otherlv_90=(Token)match(input,RightCurlyBracket,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_90, grammarAccess.getAtomicExprAccess().getRightCurlyBracketKeyword_16_2());
                          
                    }

                    }


                    }
                    break;
                case 18 :
                    // InternalResoluteParser.g:3574:6: ( () otherlv_92= Let ( (lv_binding_93_0= ruleLetBinding ) ) otherlv_94= Semicolon ( (lv_expr_95_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:3574:6: ( () otherlv_92= Let ( (lv_binding_93_0= ruleLetBinding ) ) otherlv_94= Semicolon ( (lv_expr_95_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:3574:7: () otherlv_92= Let ( (lv_binding_93_0= ruleLetBinding ) ) otherlv_94= Semicolon ( (lv_expr_95_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:3574:7: ()
                    // InternalResoluteParser.g:3575:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAtomicExprAccess().getLetExprAction_17_0(),
                                  current);
                          
                    }

                    }

                    otherlv_92=(Token)match(input,Let,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_92, grammarAccess.getAtomicExprAccess().getLetKeyword_17_1());
                          
                    }
                    // InternalResoluteParser.g:3585:1: ( (lv_binding_93_0= ruleLetBinding ) )
                    // InternalResoluteParser.g:3586:1: (lv_binding_93_0= ruleLetBinding )
                    {
                    // InternalResoluteParser.g:3586:1: (lv_binding_93_0= ruleLetBinding )
                    // InternalResoluteParser.g:3587:3: lv_binding_93_0= ruleLetBinding
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getBindingLetBindingParserRuleCall_17_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_47);
                    lv_binding_93_0=ruleLetBinding();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"binding",
                              		lv_binding_93_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.LetBinding");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_94=(Token)match(input,Semicolon,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_94, grammarAccess.getAtomicExprAccess().getSemicolonKeyword_17_3());
                          
                    }
                    // InternalResoluteParser.g:3608:1: ( (lv_expr_95_0= ruleExpr ) )
                    // InternalResoluteParser.g:3609:1: (lv_expr_95_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:3609:1: (lv_expr_95_0= ruleExpr )
                    // InternalResoluteParser.g:3610:3: lv_expr_95_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAtomicExprAccess().getExprExprParserRuleCall_17_4_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_95_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAtomicExprRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_95_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 19 :
                    // InternalResoluteParser.g:3627:6: (otherlv_96= LeftParenthesis this_Expr_97= ruleExpr otherlv_98= RightParenthesis )
                    {
                    // InternalResoluteParser.g:3627:6: (otherlv_96= LeftParenthesis this_Expr_97= ruleExpr otherlv_98= RightParenthesis )
                    // InternalResoluteParser.g:3628:2: otherlv_96= LeftParenthesis this_Expr_97= ruleExpr otherlv_98= RightParenthesis
                    {
                    otherlv_96=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_96, grammarAccess.getAtomicExprAccess().getLeftParenthesisKeyword_18_0());
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAtomicExprAccess().getExprParserRuleCall_18_1()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_34);
                    this_Expr_97=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_Expr_97;
                              afterParserOrEnumRuleCall();
                          
                    }
                    otherlv_98=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_98, grammarAccess.getAtomicExprAccess().getRightParenthesisKeyword_18_2());
                          
                    }

                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAtomicExpr"


    // $ANTLR start "entryRuleLetBinding"
    // InternalResoluteParser.g:3654:1: entryRuleLetBinding returns [EObject current=null] : iv_ruleLetBinding= ruleLetBinding EOF ;
    public final EObject entryRuleLetBinding() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLetBinding = null;


        try {
            // InternalResoluteParser.g:3655:2: (iv_ruleLetBinding= ruleLetBinding EOF )
            // InternalResoluteParser.g:3656:2: iv_ruleLetBinding= ruleLetBinding EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getLetBindingRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleLetBinding=ruleLetBinding();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleLetBinding; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLetBinding"


    // $ANTLR start "ruleLetBinding"
    // InternalResoluteParser.g:3663:1: ruleLetBinding returns [EObject current=null] : ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) ) ;
    public final EObject ruleLetBinding() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_type_2_0 = null;

        EObject lv_expr_4_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:3666:28: ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) ) )
            // InternalResoluteParser.g:3667:1: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) )
            {
            // InternalResoluteParser.g:3667:1: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) ) )
            // InternalResoluteParser.g:3667:2: ( (lv_name_0_0= RULE_ID ) ) otherlv_1= Colon ( (lv_type_2_0= ruleType ) ) otherlv_3= EqualsSign ( (lv_expr_4_0= ruleExpr ) )
            {
            // InternalResoluteParser.g:3667:2: ( (lv_name_0_0= RULE_ID ) )
            // InternalResoluteParser.g:3668:1: (lv_name_0_0= RULE_ID )
            {
            // InternalResoluteParser.g:3668:1: (lv_name_0_0= RULE_ID )
            // InternalResoluteParser.g:3669:3: lv_name_0_0= RULE_ID
            {
            lv_name_0_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_11); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_0_0, grammarAccess.getLetBindingAccess().getNameIDTerminalRuleCall_0_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getLetBindingRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_0_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.ID");
              	    
            }

            }


            }

            otherlv_1=(Token)match(input,Colon,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getLetBindingAccess().getColonKeyword_1());
                  
            }
            // InternalResoluteParser.g:3690:1: ( (lv_type_2_0= ruleType ) )
            // InternalResoluteParser.g:3691:1: (lv_type_2_0= ruleType )
            {
            // InternalResoluteParser.g:3691:1: (lv_type_2_0= ruleType )
            // InternalResoluteParser.g:3692:3: lv_type_2_0= ruleType
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getLetBindingAccess().getTypeTypeParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_13);
            lv_type_2_0=ruleType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getLetBindingRule());
              	        }
                     		set(
                     			current, 
                     			"type",
                      		lv_type_2_0, 
                      		"com.rockwellcollins.atc.resolute.Resolute.Type");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            otherlv_3=(Token)match(input,EqualsSign,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_3, grammarAccess.getLetBindingAccess().getEqualsSignKeyword_3());
                  
            }
            // InternalResoluteParser.g:3713:1: ( (lv_expr_4_0= ruleExpr ) )
            // InternalResoluteParser.g:3714:1: (lv_expr_4_0= ruleExpr )
            {
            // InternalResoluteParser.g:3714:1: (lv_expr_4_0= ruleExpr )
            // InternalResoluteParser.g:3715:3: lv_expr_4_0= ruleExpr
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getLetBindingAccess().getExprExprParserRuleCall_4_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_expr_4_0=ruleExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getLetBindingRule());
              	        }
                     		set(
                     			current, 
                     			"expr",
                      		lv_expr_4_0, 
                      		"com.rockwellcollins.atc.resolute.Resolute.Expr");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLetBinding"


    // $ANTLR start "entryRuleBuiltInFn"
    // InternalResoluteParser.g:3739:1: entryRuleBuiltInFn returns [String current=null] : iv_ruleBuiltInFn= ruleBuiltInFn EOF ;
    public final String entryRuleBuiltInFn() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleBuiltInFn = null;


        try {
            // InternalResoluteParser.g:3740:1: (iv_ruleBuiltInFn= ruleBuiltInFn EOF )
            // InternalResoluteParser.g:3741:2: iv_ruleBuiltInFn= ruleBuiltInFn EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBuiltInFnRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleBuiltInFn=ruleBuiltInFn();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBuiltInFn.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBuiltInFn"


    // $ANTLR start "ruleBuiltInFn"
    // InternalResoluteParser.g:3748:1: ruleBuiltInFn returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= Has_property | kw= Property | kw= Property_member | kw= Has_parent | kw= Parent | kw= Name | kw= Type | kw= Has_type | kw= Is_in_array | kw= Has_prototypes | kw= Has_modes | kw= Is_processor | kw= Is_virtual_processor | kw= Is_system | kw= Is_bus | kw= Is_virtual_bus | kw= Is_device | kw= Is_memory | kw= Is_thread | kw= Is_thread_group | kw= Is_process | kw= Is_data | kw= Is_subprogram | kw= Is_of_type | kw= Is_bound_to | kw= Has_member | kw= Features | kw= Connections | kw= Enumerated_values | kw= Subcomponents | kw= Source | kw= Destination | kw= Is_bidirectional | kw= Direction | kw= Is_event_port | kw= Is_abstract_feature | kw= Is_data_port | kw= Is_port | kw= Is_data_access | kw= Is_bus_access | kw= Lower_bound | kw= Upper_bound | kw= Member | kw= Length | kw= Size | kw= Sum | kw= Append | kw= Head | kw= Tail | kw= As_set | kw= Union | kw= Intersect | kw= As_list | kw= Instance | kw= Instances | kw= Debug | kw= Analysis | kw= Receive_error | kw= Contain_error | kw= Propagate_error | kw= Error_state_reachable | kw= Flow_source | kw= Flow_destination | kw= Flow_elements | kw= Flow_specifications | kw= End_to_end_flows ) ;
    public final AntlrDatatypeRuleToken ruleBuiltInFn() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:3752:6: ( (kw= Has_property | kw= Property | kw= Property_member | kw= Has_parent | kw= Parent | kw= Name | kw= Type | kw= Has_type | kw= Is_in_array | kw= Has_prototypes | kw= Has_modes | kw= Is_processor | kw= Is_virtual_processor | kw= Is_system | kw= Is_bus | kw= Is_virtual_bus | kw= Is_device | kw= Is_memory | kw= Is_thread | kw= Is_thread_group | kw= Is_process | kw= Is_data | kw= Is_subprogram | kw= Is_of_type | kw= Is_bound_to | kw= Has_member | kw= Features | kw= Connections | kw= Enumerated_values | kw= Subcomponents | kw= Source | kw= Destination | kw= Is_bidirectional | kw= Direction | kw= Is_event_port | kw= Is_abstract_feature | kw= Is_data_port | kw= Is_port | kw= Is_data_access | kw= Is_bus_access | kw= Lower_bound | kw= Upper_bound | kw= Member | kw= Length | kw= Size | kw= Sum | kw= Append | kw= Head | kw= Tail | kw= As_set | kw= Union | kw= Intersect | kw= As_list | kw= Instance | kw= Instances | kw= Debug | kw= Analysis | kw= Receive_error | kw= Contain_error | kw= Propagate_error | kw= Error_state_reachable | kw= Flow_source | kw= Flow_destination | kw= Flow_elements | kw= Flow_specifications | kw= End_to_end_flows ) )
            // InternalResoluteParser.g:3753:1: (kw= Has_property | kw= Property | kw= Property_member | kw= Has_parent | kw= Parent | kw= Name | kw= Type | kw= Has_type | kw= Is_in_array | kw= Has_prototypes | kw= Has_modes | kw= Is_processor | kw= Is_virtual_processor | kw= Is_system | kw= Is_bus | kw= Is_virtual_bus | kw= Is_device | kw= Is_memory | kw= Is_thread | kw= Is_thread_group | kw= Is_process | kw= Is_data | kw= Is_subprogram | kw= Is_of_type | kw= Is_bound_to | kw= Has_member | kw= Features | kw= Connections | kw= Enumerated_values | kw= Subcomponents | kw= Source | kw= Destination | kw= Is_bidirectional | kw= Direction | kw= Is_event_port | kw= Is_abstract_feature | kw= Is_data_port | kw= Is_port | kw= Is_data_access | kw= Is_bus_access | kw= Lower_bound | kw= Upper_bound | kw= Member | kw= Length | kw= Size | kw= Sum | kw= Append | kw= Head | kw= Tail | kw= As_set | kw= Union | kw= Intersect | kw= As_list | kw= Instance | kw= Instances | kw= Debug | kw= Analysis | kw= Receive_error | kw= Contain_error | kw= Propagate_error | kw= Error_state_reachable | kw= Flow_source | kw= Flow_destination | kw= Flow_elements | kw= Flow_specifications | kw= End_to_end_flows )
            {
            // InternalResoluteParser.g:3753:1: (kw= Has_property | kw= Property | kw= Property_member | kw= Has_parent | kw= Parent | kw= Name | kw= Type | kw= Has_type | kw= Is_in_array | kw= Has_prototypes | kw= Has_modes | kw= Is_processor | kw= Is_virtual_processor | kw= Is_system | kw= Is_bus | kw= Is_virtual_bus | kw= Is_device | kw= Is_memory | kw= Is_thread | kw= Is_thread_group | kw= Is_process | kw= Is_data | kw= Is_subprogram | kw= Is_of_type | kw= Is_bound_to | kw= Has_member | kw= Features | kw= Connections | kw= Enumerated_values | kw= Subcomponents | kw= Source | kw= Destination | kw= Is_bidirectional | kw= Direction | kw= Is_event_port | kw= Is_abstract_feature | kw= Is_data_port | kw= Is_port | kw= Is_data_access | kw= Is_bus_access | kw= Lower_bound | kw= Upper_bound | kw= Member | kw= Length | kw= Size | kw= Sum | kw= Append | kw= Head | kw= Tail | kw= As_set | kw= Union | kw= Intersect | kw= As_list | kw= Instance | kw= Instances | kw= Debug | kw= Analysis | kw= Receive_error | kw= Contain_error | kw= Propagate_error | kw= Error_state_reachable | kw= Flow_source | kw= Flow_destination | kw= Flow_elements | kw= Flow_specifications | kw= End_to_end_flows )
            int alt50=66;
            switch ( input.LA(1) ) {
            case Has_property:
                {
                alt50=1;
                }
                break;
            case Property:
                {
                alt50=2;
                }
                break;
            case Property_member:
                {
                alt50=3;
                }
                break;
            case Has_parent:
                {
                alt50=4;
                }
                break;
            case Parent:
                {
                alt50=5;
                }
                break;
            case Name:
                {
                alt50=6;
                }
                break;
            case Type:
                {
                alt50=7;
                }
                break;
            case Has_type:
                {
                alt50=8;
                }
                break;
            case Is_in_array:
                {
                alt50=9;
                }
                break;
            case Has_prototypes:
                {
                alt50=10;
                }
                break;
            case Has_modes:
                {
                alt50=11;
                }
                break;
            case Is_processor:
                {
                alt50=12;
                }
                break;
            case Is_virtual_processor:
                {
                alt50=13;
                }
                break;
            case Is_system:
                {
                alt50=14;
                }
                break;
            case Is_bus:
                {
                alt50=15;
                }
                break;
            case Is_virtual_bus:
                {
                alt50=16;
                }
                break;
            case Is_device:
                {
                alt50=17;
                }
                break;
            case Is_memory:
                {
                alt50=18;
                }
                break;
            case Is_thread:
                {
                alt50=19;
                }
                break;
            case Is_thread_group:
                {
                alt50=20;
                }
                break;
            case Is_process:
                {
                alt50=21;
                }
                break;
            case Is_data:
                {
                alt50=22;
                }
                break;
            case Is_subprogram:
                {
                alt50=23;
                }
                break;
            case Is_of_type:
                {
                alt50=24;
                }
                break;
            case Is_bound_to:
                {
                alt50=25;
                }
                break;
            case Has_member:
                {
                alt50=26;
                }
                break;
            case Features:
                {
                alt50=27;
                }
                break;
            case Connections:
                {
                alt50=28;
                }
                break;
            case Enumerated_values:
                {
                alt50=29;
                }
                break;
            case Subcomponents:
                {
                alt50=30;
                }
                break;
            case Source:
                {
                alt50=31;
                }
                break;
            case Destination:
                {
                alt50=32;
                }
                break;
            case Is_bidirectional:
                {
                alt50=33;
                }
                break;
            case Direction:
                {
                alt50=34;
                }
                break;
            case Is_event_port:
                {
                alt50=35;
                }
                break;
            case Is_abstract_feature:
                {
                alt50=36;
                }
                break;
            case Is_data_port:
                {
                alt50=37;
                }
                break;
            case Is_port:
                {
                alt50=38;
                }
                break;
            case Is_data_access:
                {
                alt50=39;
                }
                break;
            case Is_bus_access:
                {
                alt50=40;
                }
                break;
            case Lower_bound:
                {
                alt50=41;
                }
                break;
            case Upper_bound:
                {
                alt50=42;
                }
                break;
            case Member:
                {
                alt50=43;
                }
                break;
            case Length:
                {
                alt50=44;
                }
                break;
            case Size:
                {
                alt50=45;
                }
                break;
            case Sum:
                {
                alt50=46;
                }
                break;
            case Append:
                {
                alt50=47;
                }
                break;
            case Head:
                {
                alt50=48;
                }
                break;
            case Tail:
                {
                alt50=49;
                }
                break;
            case As_set:
                {
                alt50=50;
                }
                break;
            case Union:
                {
                alt50=51;
                }
                break;
            case Intersect:
                {
                alt50=52;
                }
                break;
            case As_list:
                {
                alt50=53;
                }
                break;
            case Instance:
                {
                alt50=54;
                }
                break;
            case Instances:
                {
                alt50=55;
                }
                break;
            case Debug:
                {
                alt50=56;
                }
                break;
            case Analysis:
                {
                alt50=57;
                }
                break;
            case Receive_error:
                {
                alt50=58;
                }
                break;
            case Contain_error:
                {
                alt50=59;
                }
                break;
            case Propagate_error:
                {
                alt50=60;
                }
                break;
            case Error_state_reachable:
                {
                alt50=61;
                }
                break;
            case Flow_source:
                {
                alt50=62;
                }
                break;
            case Flow_destination:
                {
                alt50=63;
                }
                break;
            case Flow_elements:
                {
                alt50=64;
                }
                break;
            case Flow_specifications:
                {
                alt50=65;
                }
                break;
            case End_to_end_flows:
                {
                alt50=66;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }

            switch (alt50) {
                case 1 :
                    // InternalResoluteParser.g:3754:2: kw= Has_property
                    {
                    kw=(Token)match(input,Has_property,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getHas_propertyKeyword_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:3761:2: kw= Property
                    {
                    kw=(Token)match(input,Property,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getPropertyKeyword_1()); 
                          
                    }

                    }
                    break;
                case 3 :
                    // InternalResoluteParser.g:3768:2: kw= Property_member
                    {
                    kw=(Token)match(input,Property_member,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getProperty_memberKeyword_2()); 
                          
                    }

                    }
                    break;
                case 4 :
                    // InternalResoluteParser.g:3775:2: kw= Has_parent
                    {
                    kw=(Token)match(input,Has_parent,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getHas_parentKeyword_3()); 
                          
                    }

                    }
                    break;
                case 5 :
                    // InternalResoluteParser.g:3782:2: kw= Parent
                    {
                    kw=(Token)match(input,Parent,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getParentKeyword_4()); 
                          
                    }

                    }
                    break;
                case 6 :
                    // InternalResoluteParser.g:3789:2: kw= Name
                    {
                    kw=(Token)match(input,Name,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getNameKeyword_5()); 
                          
                    }

                    }
                    break;
                case 7 :
                    // InternalResoluteParser.g:3796:2: kw= Type
                    {
                    kw=(Token)match(input,Type,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getTypeKeyword_6()); 
                          
                    }

                    }
                    break;
                case 8 :
                    // InternalResoluteParser.g:3803:2: kw= Has_type
                    {
                    kw=(Token)match(input,Has_type,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getHas_typeKeyword_7()); 
                          
                    }

                    }
                    break;
                case 9 :
                    // InternalResoluteParser.g:3810:2: kw= Is_in_array
                    {
                    kw=(Token)match(input,Is_in_array,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_in_arrayKeyword_8()); 
                          
                    }

                    }
                    break;
                case 10 :
                    // InternalResoluteParser.g:3817:2: kw= Has_prototypes
                    {
                    kw=(Token)match(input,Has_prototypes,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getHas_prototypesKeyword_9()); 
                          
                    }

                    }
                    break;
                case 11 :
                    // InternalResoluteParser.g:3824:2: kw= Has_modes
                    {
                    kw=(Token)match(input,Has_modes,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getHas_modesKeyword_10()); 
                          
                    }

                    }
                    break;
                case 12 :
                    // InternalResoluteParser.g:3831:2: kw= Is_processor
                    {
                    kw=(Token)match(input,Is_processor,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_processorKeyword_11()); 
                          
                    }

                    }
                    break;
                case 13 :
                    // InternalResoluteParser.g:3838:2: kw= Is_virtual_processor
                    {
                    kw=(Token)match(input,Is_virtual_processor,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_virtual_processorKeyword_12()); 
                          
                    }

                    }
                    break;
                case 14 :
                    // InternalResoluteParser.g:3845:2: kw= Is_system
                    {
                    kw=(Token)match(input,Is_system,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_systemKeyword_13()); 
                          
                    }

                    }
                    break;
                case 15 :
                    // InternalResoluteParser.g:3852:2: kw= Is_bus
                    {
                    kw=(Token)match(input,Is_bus,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_busKeyword_14()); 
                          
                    }

                    }
                    break;
                case 16 :
                    // InternalResoluteParser.g:3859:2: kw= Is_virtual_bus
                    {
                    kw=(Token)match(input,Is_virtual_bus,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_virtual_busKeyword_15()); 
                          
                    }

                    }
                    break;
                case 17 :
                    // InternalResoluteParser.g:3866:2: kw= Is_device
                    {
                    kw=(Token)match(input,Is_device,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_deviceKeyword_16()); 
                          
                    }

                    }
                    break;
                case 18 :
                    // InternalResoluteParser.g:3873:2: kw= Is_memory
                    {
                    kw=(Token)match(input,Is_memory,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_memoryKeyword_17()); 
                          
                    }

                    }
                    break;
                case 19 :
                    // InternalResoluteParser.g:3880:2: kw= Is_thread
                    {
                    kw=(Token)match(input,Is_thread,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_threadKeyword_18()); 
                          
                    }

                    }
                    break;
                case 20 :
                    // InternalResoluteParser.g:3887:2: kw= Is_thread_group
                    {
                    kw=(Token)match(input,Is_thread_group,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_thread_groupKeyword_19()); 
                          
                    }

                    }
                    break;
                case 21 :
                    // InternalResoluteParser.g:3894:2: kw= Is_process
                    {
                    kw=(Token)match(input,Is_process,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_processKeyword_20()); 
                          
                    }

                    }
                    break;
                case 22 :
                    // InternalResoluteParser.g:3901:2: kw= Is_data
                    {
                    kw=(Token)match(input,Is_data,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_dataKeyword_21()); 
                          
                    }

                    }
                    break;
                case 23 :
                    // InternalResoluteParser.g:3908:2: kw= Is_subprogram
                    {
                    kw=(Token)match(input,Is_subprogram,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_subprogramKeyword_22()); 
                          
                    }

                    }
                    break;
                case 24 :
                    // InternalResoluteParser.g:3915:2: kw= Is_of_type
                    {
                    kw=(Token)match(input,Is_of_type,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_of_typeKeyword_23()); 
                          
                    }

                    }
                    break;
                case 25 :
                    // InternalResoluteParser.g:3922:2: kw= Is_bound_to
                    {
                    kw=(Token)match(input,Is_bound_to,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_bound_toKeyword_24()); 
                          
                    }

                    }
                    break;
                case 26 :
                    // InternalResoluteParser.g:3929:2: kw= Has_member
                    {
                    kw=(Token)match(input,Has_member,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getHas_memberKeyword_25()); 
                          
                    }

                    }
                    break;
                case 27 :
                    // InternalResoluteParser.g:3936:2: kw= Features
                    {
                    kw=(Token)match(input,Features,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getFeaturesKeyword_26()); 
                          
                    }

                    }
                    break;
                case 28 :
                    // InternalResoluteParser.g:3943:2: kw= Connections
                    {
                    kw=(Token)match(input,Connections,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getConnectionsKeyword_27()); 
                          
                    }

                    }
                    break;
                case 29 :
                    // InternalResoluteParser.g:3950:2: kw= Enumerated_values
                    {
                    kw=(Token)match(input,Enumerated_values,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getEnumerated_valuesKeyword_28()); 
                          
                    }

                    }
                    break;
                case 30 :
                    // InternalResoluteParser.g:3957:2: kw= Subcomponents
                    {
                    kw=(Token)match(input,Subcomponents,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getSubcomponentsKeyword_29()); 
                          
                    }

                    }
                    break;
                case 31 :
                    // InternalResoluteParser.g:3964:2: kw= Source
                    {
                    kw=(Token)match(input,Source,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getSourceKeyword_30()); 
                          
                    }

                    }
                    break;
                case 32 :
                    // InternalResoluteParser.g:3971:2: kw= Destination
                    {
                    kw=(Token)match(input,Destination,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getDestinationKeyword_31()); 
                          
                    }

                    }
                    break;
                case 33 :
                    // InternalResoluteParser.g:3978:2: kw= Is_bidirectional
                    {
                    kw=(Token)match(input,Is_bidirectional,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_bidirectionalKeyword_32()); 
                          
                    }

                    }
                    break;
                case 34 :
                    // InternalResoluteParser.g:3985:2: kw= Direction
                    {
                    kw=(Token)match(input,Direction,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getDirectionKeyword_33()); 
                          
                    }

                    }
                    break;
                case 35 :
                    // InternalResoluteParser.g:3992:2: kw= Is_event_port
                    {
                    kw=(Token)match(input,Is_event_port,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_event_portKeyword_34()); 
                          
                    }

                    }
                    break;
                case 36 :
                    // InternalResoluteParser.g:3999:2: kw= Is_abstract_feature
                    {
                    kw=(Token)match(input,Is_abstract_feature,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_abstract_featureKeyword_35()); 
                          
                    }

                    }
                    break;
                case 37 :
                    // InternalResoluteParser.g:4006:2: kw= Is_data_port
                    {
                    kw=(Token)match(input,Is_data_port,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_data_portKeyword_36()); 
                          
                    }

                    }
                    break;
                case 38 :
                    // InternalResoluteParser.g:4013:2: kw= Is_port
                    {
                    kw=(Token)match(input,Is_port,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_portKeyword_37()); 
                          
                    }

                    }
                    break;
                case 39 :
                    // InternalResoluteParser.g:4020:2: kw= Is_data_access
                    {
                    kw=(Token)match(input,Is_data_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_data_accessKeyword_38()); 
                          
                    }

                    }
                    break;
                case 40 :
                    // InternalResoluteParser.g:4027:2: kw= Is_bus_access
                    {
                    kw=(Token)match(input,Is_bus_access,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIs_bus_accessKeyword_39()); 
                          
                    }

                    }
                    break;
                case 41 :
                    // InternalResoluteParser.g:4034:2: kw= Lower_bound
                    {
                    kw=(Token)match(input,Lower_bound,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getLower_boundKeyword_40()); 
                          
                    }

                    }
                    break;
                case 42 :
                    // InternalResoluteParser.g:4041:2: kw= Upper_bound
                    {
                    kw=(Token)match(input,Upper_bound,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getUpper_boundKeyword_41()); 
                          
                    }

                    }
                    break;
                case 43 :
                    // InternalResoluteParser.g:4048:2: kw= Member
                    {
                    kw=(Token)match(input,Member,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getMemberKeyword_42()); 
                          
                    }

                    }
                    break;
                case 44 :
                    // InternalResoluteParser.g:4055:2: kw= Length
                    {
                    kw=(Token)match(input,Length,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getLengthKeyword_43()); 
                          
                    }

                    }
                    break;
                case 45 :
                    // InternalResoluteParser.g:4062:2: kw= Size
                    {
                    kw=(Token)match(input,Size,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getSizeKeyword_44()); 
                          
                    }

                    }
                    break;
                case 46 :
                    // InternalResoluteParser.g:4069:2: kw= Sum
                    {
                    kw=(Token)match(input,Sum,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getSumKeyword_45()); 
                          
                    }

                    }
                    break;
                case 47 :
                    // InternalResoluteParser.g:4076:2: kw= Append
                    {
                    kw=(Token)match(input,Append,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getAppendKeyword_46()); 
                          
                    }

                    }
                    break;
                case 48 :
                    // InternalResoluteParser.g:4083:2: kw= Head
                    {
                    kw=(Token)match(input,Head,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getHeadKeyword_47()); 
                          
                    }

                    }
                    break;
                case 49 :
                    // InternalResoluteParser.g:4090:2: kw= Tail
                    {
                    kw=(Token)match(input,Tail,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getTailKeyword_48()); 
                          
                    }

                    }
                    break;
                case 50 :
                    // InternalResoluteParser.g:4097:2: kw= As_set
                    {
                    kw=(Token)match(input,As_set,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getAs_setKeyword_49()); 
                          
                    }

                    }
                    break;
                case 51 :
                    // InternalResoluteParser.g:4104:2: kw= Union
                    {
                    kw=(Token)match(input,Union,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getUnionKeyword_50()); 
                          
                    }

                    }
                    break;
                case 52 :
                    // InternalResoluteParser.g:4111:2: kw= Intersect
                    {
                    kw=(Token)match(input,Intersect,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getIntersectKeyword_51()); 
                          
                    }

                    }
                    break;
                case 53 :
                    // InternalResoluteParser.g:4118:2: kw= As_list
                    {
                    kw=(Token)match(input,As_list,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getAs_listKeyword_52()); 
                          
                    }

                    }
                    break;
                case 54 :
                    // InternalResoluteParser.g:4125:2: kw= Instance
                    {
                    kw=(Token)match(input,Instance,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getInstanceKeyword_53()); 
                          
                    }

                    }
                    break;
                case 55 :
                    // InternalResoluteParser.g:4132:2: kw= Instances
                    {
                    kw=(Token)match(input,Instances,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getInstancesKeyword_54()); 
                          
                    }

                    }
                    break;
                case 56 :
                    // InternalResoluteParser.g:4139:2: kw= Debug
                    {
                    kw=(Token)match(input,Debug,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getDebugKeyword_55()); 
                          
                    }

                    }
                    break;
                case 57 :
                    // InternalResoluteParser.g:4146:2: kw= Analysis
                    {
                    kw=(Token)match(input,Analysis,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getAnalysisKeyword_56()); 
                          
                    }

                    }
                    break;
                case 58 :
                    // InternalResoluteParser.g:4153:2: kw= Receive_error
                    {
                    kw=(Token)match(input,Receive_error,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getReceive_errorKeyword_57()); 
                          
                    }

                    }
                    break;
                case 59 :
                    // InternalResoluteParser.g:4160:2: kw= Contain_error
                    {
                    kw=(Token)match(input,Contain_error,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getContain_errorKeyword_58()); 
                          
                    }

                    }
                    break;
                case 60 :
                    // InternalResoluteParser.g:4167:2: kw= Propagate_error
                    {
                    kw=(Token)match(input,Propagate_error,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getPropagate_errorKeyword_59()); 
                          
                    }

                    }
                    break;
                case 61 :
                    // InternalResoluteParser.g:4174:2: kw= Error_state_reachable
                    {
                    kw=(Token)match(input,Error_state_reachable,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getError_state_reachableKeyword_60()); 
                          
                    }

                    }
                    break;
                case 62 :
                    // InternalResoluteParser.g:4181:2: kw= Flow_source
                    {
                    kw=(Token)match(input,Flow_source,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getFlow_sourceKeyword_61()); 
                          
                    }

                    }
                    break;
                case 63 :
                    // InternalResoluteParser.g:4188:2: kw= Flow_destination
                    {
                    kw=(Token)match(input,Flow_destination,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getFlow_destinationKeyword_62()); 
                          
                    }

                    }
                    break;
                case 64 :
                    // InternalResoluteParser.g:4195:2: kw= Flow_elements
                    {
                    kw=(Token)match(input,Flow_elements,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getFlow_elementsKeyword_63()); 
                          
                    }

                    }
                    break;
                case 65 :
                    // InternalResoluteParser.g:4202:2: kw= Flow_specifications
                    {
                    kw=(Token)match(input,Flow_specifications,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getFlow_specificationsKeyword_64()); 
                          
                    }

                    }
                    break;
                case 66 :
                    // InternalResoluteParser.g:4209:2: kw= End_to_end_flows
                    {
                    kw=(Token)match(input,End_to_end_flows,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInFnAccess().getEnd_to_end_flowsKeyword_65()); 
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBuiltInFn"


    // $ANTLR start "entryRuleRealTerm"
    // InternalResoluteParser.g:4222:1: entryRuleRealTerm returns [EObject current=null] : iv_ruleRealTerm= ruleRealTerm EOF ;
    public final EObject entryRuleRealTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRealTerm = null;


        try {
            // InternalResoluteParser.g:4223:2: (iv_ruleRealTerm= ruleRealTerm EOF )
            // InternalResoluteParser.g:4224:2: iv_ruleRealTerm= ruleRealTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRealTermRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleRealTerm=ruleRealTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRealTerm; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRealTerm"


    // $ANTLR start "ruleRealTerm"
    // InternalResoluteParser.g:4231:1: ruleRealTerm returns [EObject current=null] : ( ( (lv_value_0_0= ruleUnsignedReal ) ) ( (otherlv_1= RULE_ID ) )? ) ;
    public final EObject ruleRealTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        AntlrDatatypeRuleToken lv_value_0_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4234:28: ( ( ( (lv_value_0_0= ruleUnsignedReal ) ) ( (otherlv_1= RULE_ID ) )? ) )
            // InternalResoluteParser.g:4235:1: ( ( (lv_value_0_0= ruleUnsignedReal ) ) ( (otherlv_1= RULE_ID ) )? )
            {
            // InternalResoluteParser.g:4235:1: ( ( (lv_value_0_0= ruleUnsignedReal ) ) ( (otherlv_1= RULE_ID ) )? )
            // InternalResoluteParser.g:4235:2: ( (lv_value_0_0= ruleUnsignedReal ) ) ( (otherlv_1= RULE_ID ) )?
            {
            // InternalResoluteParser.g:4235:2: ( (lv_value_0_0= ruleUnsignedReal ) )
            // InternalResoluteParser.g:4236:1: (lv_value_0_0= ruleUnsignedReal )
            {
            // InternalResoluteParser.g:4236:1: (lv_value_0_0= ruleUnsignedReal )
            // InternalResoluteParser.g:4237:3: lv_value_0_0= ruleUnsignedReal
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getRealTermAccess().getValueUnsignedRealParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_48);
            lv_value_0_0=ruleUnsignedReal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getRealTermRule());
              	        }
                     		set(
                     			current, 
                     			"value",
                      		lv_value_0_0, 
                      		"com.rockwellcollins.atc.resolute.Resolute.UnsignedReal");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // InternalResoluteParser.g:4253:2: ( (otherlv_1= RULE_ID ) )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==RULE_ID) ) {
                int LA51_1 = input.LA(2);

                if ( (LA51_1==EOF||LA51_1==Instanceof||(LA51_1>=Andthen && LA51_1<=Applies)||(LA51_1>=Ruleset && LA51_1<=Warning)||LA51_1==Orelse||LA51_1==Check||(LA51_1>=Delta && LA51_1<=Error)||LA51_1==Prove||LA51_1==Else||LA51_1==Info||LA51_1==Then||LA51_1==And||LA51_1==For||LA51_1==FullStopFullStop||(LA51_1>=LessThanSignEqualsSign && LA51_1<=GreaterThanSignEqualsSign)||(LA51_1>=In && LA51_1<=Or)||LA51_1==PercentSign||(LA51_1>=RightParenthesis && LA51_1<=HyphenMinus)||LA51_1==Solidus||(LA51_1>=Semicolon && LA51_1<=GreaterThanSign)||(LA51_1>=RightSquareBracket && LA51_1<=CircumflexAccent)||LA51_1==RightCurlyBracket||LA51_1==RULE_ID) ) {
                    alt51=1;
                }
            }
            switch (alt51) {
                case 1 :
                    // InternalResoluteParser.g:4254:1: (otherlv_1= RULE_ID )
                    {
                    // InternalResoluteParser.g:4254:1: (otherlv_1= RULE_ID )
                    // InternalResoluteParser.g:4255:3: otherlv_1= RULE_ID
                    {
                    if ( state.backtracking==0 ) {

                      			if (current==null) {
                      	            current = createModelElement(grammarAccess.getRealTermRule());
                      	        }
                              
                    }
                    otherlv_1=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		newLeafNode(otherlv_1, grammarAccess.getRealTermAccess().getUnitUnitLiteralCrossReference_1_0()); 
                      	
                    }

                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRealTerm"


    // $ANTLR start "entryRuleUnsignedReal"
    // InternalResoluteParser.g:4274:1: entryRuleUnsignedReal returns [String current=null] : iv_ruleUnsignedReal= ruleUnsignedReal EOF ;
    public final String entryRuleUnsignedReal() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUnsignedReal = null;


        try {
            // InternalResoluteParser.g:4275:1: (iv_ruleUnsignedReal= ruleUnsignedReal EOF )
            // InternalResoluteParser.g:4276:2: iv_ruleUnsignedReal= ruleUnsignedReal EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getUnsignedRealRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleUnsignedReal=ruleUnsignedReal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleUnsignedReal.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUnsignedReal"


    // $ANTLR start "ruleUnsignedReal"
    // InternalResoluteParser.g:4283:1: ruleUnsignedReal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_REAL_LIT_0= RULE_REAL_LIT ;
    public final AntlrDatatypeRuleToken ruleUnsignedReal() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_REAL_LIT_0=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4287:6: (this_REAL_LIT_0= RULE_REAL_LIT )
            // InternalResoluteParser.g:4288:5: this_REAL_LIT_0= RULE_REAL_LIT
            {
            this_REAL_LIT_0=(Token)match(input,RULE_REAL_LIT,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_REAL_LIT_0);
                  
            }
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_REAL_LIT_0, grammarAccess.getUnsignedRealAccess().getREAL_LITTerminalRuleCall()); 
                  
            }

            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUnsignedReal"


    // $ANTLR start "entryRuleIntegerTerm"
    // InternalResoluteParser.g:4303:1: entryRuleIntegerTerm returns [EObject current=null] : iv_ruleIntegerTerm= ruleIntegerTerm EOF ;
    public final EObject entryRuleIntegerTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerTerm = null;


        try {
            // InternalResoluteParser.g:4304:2: (iv_ruleIntegerTerm= ruleIntegerTerm EOF )
            // InternalResoluteParser.g:4305:2: iv_ruleIntegerTerm= ruleIntegerTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIntegerTermRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleIntegerTerm=ruleIntegerTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIntegerTerm; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleIntegerTerm"


    // $ANTLR start "ruleIntegerTerm"
    // InternalResoluteParser.g:4312:1: ruleIntegerTerm returns [EObject current=null] : ( ( (lv_value_0_0= ruleUnsignedInt ) ) ( (otherlv_1= RULE_ID ) )? ) ;
    public final EObject ruleIntegerTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        AntlrDatatypeRuleToken lv_value_0_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4315:28: ( ( ( (lv_value_0_0= ruleUnsignedInt ) ) ( (otherlv_1= RULE_ID ) )? ) )
            // InternalResoluteParser.g:4316:1: ( ( (lv_value_0_0= ruleUnsignedInt ) ) ( (otherlv_1= RULE_ID ) )? )
            {
            // InternalResoluteParser.g:4316:1: ( ( (lv_value_0_0= ruleUnsignedInt ) ) ( (otherlv_1= RULE_ID ) )? )
            // InternalResoluteParser.g:4316:2: ( (lv_value_0_0= ruleUnsignedInt ) ) ( (otherlv_1= RULE_ID ) )?
            {
            // InternalResoluteParser.g:4316:2: ( (lv_value_0_0= ruleUnsignedInt ) )
            // InternalResoluteParser.g:4317:1: (lv_value_0_0= ruleUnsignedInt )
            {
            // InternalResoluteParser.g:4317:1: (lv_value_0_0= ruleUnsignedInt )
            // InternalResoluteParser.g:4318:3: lv_value_0_0= ruleUnsignedInt
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getIntegerTermAccess().getValueUnsignedIntParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_48);
            lv_value_0_0=ruleUnsignedInt();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getIntegerTermRule());
              	        }
                     		set(
                     			current, 
                     			"value",
                      		lv_value_0_0, 
                      		"com.rockwellcollins.atc.resolute.Resolute.UnsignedInt");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // InternalResoluteParser.g:4334:2: ( (otherlv_1= RULE_ID ) )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==RULE_ID) ) {
                int LA52_1 = input.LA(2);

                if ( (LA52_1==EOF||LA52_1==Instanceof||(LA52_1>=Andthen && LA52_1<=Applies)||(LA52_1>=Ruleset && LA52_1<=Warning)||LA52_1==Orelse||LA52_1==Check||(LA52_1>=Delta && LA52_1<=Error)||LA52_1==Prove||LA52_1==Else||LA52_1==Info||LA52_1==Then||LA52_1==And||LA52_1==For||LA52_1==FullStopFullStop||(LA52_1>=LessThanSignEqualsSign && LA52_1<=GreaterThanSignEqualsSign)||(LA52_1>=In && LA52_1<=Or)||LA52_1==PercentSign||(LA52_1>=RightParenthesis && LA52_1<=HyphenMinus)||LA52_1==Solidus||(LA52_1>=Semicolon && LA52_1<=GreaterThanSign)||(LA52_1>=RightSquareBracket && LA52_1<=CircumflexAccent)||LA52_1==RightCurlyBracket||LA52_1==RULE_ID) ) {
                    alt52=1;
                }
            }
            switch (alt52) {
                case 1 :
                    // InternalResoluteParser.g:4335:1: (otherlv_1= RULE_ID )
                    {
                    // InternalResoluteParser.g:4335:1: (otherlv_1= RULE_ID )
                    // InternalResoluteParser.g:4336:3: otherlv_1= RULE_ID
                    {
                    if ( state.backtracking==0 ) {

                      			if (current==null) {
                      	            current = createModelElement(grammarAccess.getIntegerTermRule());
                      	        }
                              
                    }
                    otherlv_1=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		newLeafNode(otherlv_1, grammarAccess.getIntegerTermAccess().getUnitUnitLiteralCrossReference_1_0()); 
                      	
                    }

                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleIntegerTerm"


    // $ANTLR start "entryRuleUnsignedInt"
    // InternalResoluteParser.g:4355:1: entryRuleUnsignedInt returns [String current=null] : iv_ruleUnsignedInt= ruleUnsignedInt EOF ;
    public final String entryRuleUnsignedInt() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUnsignedInt = null;


        try {
            // InternalResoluteParser.g:4356:1: (iv_ruleUnsignedInt= ruleUnsignedInt EOF )
            // InternalResoluteParser.g:4357:2: iv_ruleUnsignedInt= ruleUnsignedInt EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getUnsignedIntRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleUnsignedInt=ruleUnsignedInt();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleUnsignedInt.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUnsignedInt"


    // $ANTLR start "ruleUnsignedInt"
    // InternalResoluteParser.g:4364:1: ruleUnsignedInt returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INTEGER_LIT_0= RULE_INTEGER_LIT ;
    public final AntlrDatatypeRuleToken ruleUnsignedInt() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INTEGER_LIT_0=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4368:6: (this_INTEGER_LIT_0= RULE_INTEGER_LIT )
            // InternalResoluteParser.g:4369:5: this_INTEGER_LIT_0= RULE_INTEGER_LIT
            {
            this_INTEGER_LIT_0=(Token)match(input,RULE_INTEGER_LIT,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_INTEGER_LIT_0);
                  
            }
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_INTEGER_LIT_0, grammarAccess.getUnsignedIntAccess().getINTEGER_LITTerminalRuleCall()); 
                  
            }

            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUnsignedInt"


    // $ANTLR start "entryRuleResoluteSubclause"
    // InternalResoluteParser.g:4384:1: entryRuleResoluteSubclause returns [EObject current=null] : iv_ruleResoluteSubclause= ruleResoluteSubclause EOF ;
    public final EObject entryRuleResoluteSubclause() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleResoluteSubclause = null;


        try {
            // InternalResoluteParser.g:4385:2: (iv_ruleResoluteSubclause= ruleResoluteSubclause EOF )
            // InternalResoluteParser.g:4386:2: iv_ruleResoluteSubclause= ruleResoluteSubclause EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getResoluteSubclauseRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleResoluteSubclause=ruleResoluteSubclause();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleResoluteSubclause; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleResoluteSubclause"


    // $ANTLR start "ruleResoluteSubclause"
    // InternalResoluteParser.g:4393:1: ruleResoluteSubclause returns [EObject current=null] : ( () ( (lv_proves_1_0= ruleAnalysisStatement ) )* ) ;
    public final EObject ruleResoluteSubclause() throws RecognitionException {
        EObject current = null;

        EObject lv_proves_1_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4396:28: ( ( () ( (lv_proves_1_0= ruleAnalysisStatement ) )* ) )
            // InternalResoluteParser.g:4397:1: ( () ( (lv_proves_1_0= ruleAnalysisStatement ) )* )
            {
            // InternalResoluteParser.g:4397:1: ( () ( (lv_proves_1_0= ruleAnalysisStatement ) )* )
            // InternalResoluteParser.g:4397:2: () ( (lv_proves_1_0= ruleAnalysisStatement ) )*
            {
            // InternalResoluteParser.g:4397:2: ()
            // InternalResoluteParser.g:4398:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getResoluteSubclauseAccess().getResoluteSubclauseAction_0(),
                          current);
                  
            }

            }

            // InternalResoluteParser.g:4403:2: ( (lv_proves_1_0= ruleAnalysisStatement ) )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==Check||LA53_0==Prove) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // InternalResoluteParser.g:4404:1: (lv_proves_1_0= ruleAnalysisStatement )
            	    {
            	    // InternalResoluteParser.g:4404:1: (lv_proves_1_0= ruleAnalysisStatement )
            	    // InternalResoluteParser.g:4405:3: lv_proves_1_0= ruleAnalysisStatement
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getResoluteSubclauseAccess().getProvesAnalysisStatementParserRuleCall_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_49);
            	    lv_proves_1_0=ruleAnalysisStatement();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getResoluteSubclauseRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"proves",
            	              		lv_proves_1_0, 
            	              		"com.rockwellcollins.atc.resolute.Resolute.AnalysisStatement");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop53;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleResoluteSubclause"


    // $ANTLR start "entryRuleNestedDotID"
    // InternalResoluteParser.g:4429:1: entryRuleNestedDotID returns [EObject current=null] : iv_ruleNestedDotID= ruleNestedDotID EOF ;
    public final EObject entryRuleNestedDotID() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedDotID = null;


        try {
            // InternalResoluteParser.g:4430:2: (iv_ruleNestedDotID= ruleNestedDotID EOF )
            // InternalResoluteParser.g:4431:2: iv_ruleNestedDotID= ruleNestedDotID EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNestedDotIDRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNestedDotID=ruleNestedDotID();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNestedDotID; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNestedDotID"


    // $ANTLR start "ruleNestedDotID"
    // InternalResoluteParser.g:4438:1: ruleNestedDotID returns [EObject current=null] : ( ( (otherlv_0= RULE_ID ) ) (otherlv_1= FullStop ( (lv_sub_2_0= ruleNestedDotID ) ) )? ) ;
    public final EObject ruleNestedDotID() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        EObject lv_sub_2_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4441:28: ( ( ( (otherlv_0= RULE_ID ) ) (otherlv_1= FullStop ( (lv_sub_2_0= ruleNestedDotID ) ) )? ) )
            // InternalResoluteParser.g:4442:1: ( ( (otherlv_0= RULE_ID ) ) (otherlv_1= FullStop ( (lv_sub_2_0= ruleNestedDotID ) ) )? )
            {
            // InternalResoluteParser.g:4442:1: ( ( (otherlv_0= RULE_ID ) ) (otherlv_1= FullStop ( (lv_sub_2_0= ruleNestedDotID ) ) )? )
            // InternalResoluteParser.g:4442:2: ( (otherlv_0= RULE_ID ) ) (otherlv_1= FullStop ( (lv_sub_2_0= ruleNestedDotID ) ) )?
            {
            // InternalResoluteParser.g:4442:2: ( (otherlv_0= RULE_ID ) )
            // InternalResoluteParser.g:4443:1: (otherlv_0= RULE_ID )
            {
            // InternalResoluteParser.g:4443:1: (otherlv_0= RULE_ID )
            // InternalResoluteParser.g:4444:3: otherlv_0= RULE_ID
            {
            if ( state.backtracking==0 ) {

              			if (current==null) {
              	            current = createModelElement(grammarAccess.getNestedDotIDRule());
              	        }
                      
            }
            otherlv_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_35); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		newLeafNode(otherlv_0, grammarAccess.getNestedDotIDAccess().getBaseNamedElementCrossReference_0_0()); 
              	
            }

            }


            }

            // InternalResoluteParser.g:4455:2: (otherlv_1= FullStop ( (lv_sub_2_0= ruleNestedDotID ) ) )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==FullStop) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // InternalResoluteParser.g:4456:2: otherlv_1= FullStop ( (lv_sub_2_0= ruleNestedDotID ) )
                    {
                    otherlv_1=(Token)match(input,FullStop,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getNestedDotIDAccess().getFullStopKeyword_1_0());
                          
                    }
                    // InternalResoluteParser.g:4460:1: ( (lv_sub_2_0= ruleNestedDotID ) )
                    // InternalResoluteParser.g:4461:1: (lv_sub_2_0= ruleNestedDotID )
                    {
                    // InternalResoluteParser.g:4461:1: (lv_sub_2_0= ruleNestedDotID )
                    // InternalResoluteParser.g:4462:3: lv_sub_2_0= ruleNestedDotID
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getNestedDotIDAccess().getSubNestedDotIDParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_sub_2_0=ruleNestedDotID();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getNestedDotIDRule());
                      	        }
                             		set(
                             			current, 
                             			"sub",
                              		lv_sub_2_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.NestedDotID");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNestedDotID"


    // $ANTLR start "entryRuleLintStatement"
    // InternalResoluteParser.g:4486:1: entryRuleLintStatement returns [EObject current=null] : iv_ruleLintStatement= ruleLintStatement EOF ;
    public final EObject entryRuleLintStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLintStatement = null;


        try {
            // InternalResoluteParser.g:4487:2: (iv_ruleLintStatement= ruleLintStatement EOF )
            // InternalResoluteParser.g:4488:2: iv_ruleLintStatement= ruleLintStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getLintStatementRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleLintStatement=ruleLintStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleLintStatement; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLintStatement"


    // $ANTLR start "ruleLintStatement"
    // InternalResoluteParser.g:4495:1: ruleLintStatement returns [EObject current=null] : ( ( () otherlv_1= Warning ( (lv_expr_2_0= ruleExpr ) ) ) | ( () otherlv_4= Error ( (lv_expr_5_0= ruleExpr ) ) ) | ( () otherlv_7= Info ( (lv_expr_8_0= ruleExpr ) ) ) ) ;
    public final EObject ruleLintStatement() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_4=null;
        Token otherlv_7=null;
        EObject lv_expr_2_0 = null;

        EObject lv_expr_5_0 = null;

        EObject lv_expr_8_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4498:28: ( ( ( () otherlv_1= Warning ( (lv_expr_2_0= ruleExpr ) ) ) | ( () otherlv_4= Error ( (lv_expr_5_0= ruleExpr ) ) ) | ( () otherlv_7= Info ( (lv_expr_8_0= ruleExpr ) ) ) ) )
            // InternalResoluteParser.g:4499:1: ( ( () otherlv_1= Warning ( (lv_expr_2_0= ruleExpr ) ) ) | ( () otherlv_4= Error ( (lv_expr_5_0= ruleExpr ) ) ) | ( () otherlv_7= Info ( (lv_expr_8_0= ruleExpr ) ) ) )
            {
            // InternalResoluteParser.g:4499:1: ( ( () otherlv_1= Warning ( (lv_expr_2_0= ruleExpr ) ) ) | ( () otherlv_4= Error ( (lv_expr_5_0= ruleExpr ) ) ) | ( () otherlv_7= Info ( (lv_expr_8_0= ruleExpr ) ) ) )
            int alt55=3;
            switch ( input.LA(1) ) {
            case Warning:
                {
                alt55=1;
                }
                break;
            case Error:
                {
                alt55=2;
                }
                break;
            case Info:
                {
                alt55=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }

            switch (alt55) {
                case 1 :
                    // InternalResoluteParser.g:4499:2: ( () otherlv_1= Warning ( (lv_expr_2_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:4499:2: ( () otherlv_1= Warning ( (lv_expr_2_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:4499:3: () otherlv_1= Warning ( (lv_expr_2_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:4499:3: ()
                    // InternalResoluteParser.g:4500:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getLintStatementAccess().getWarningStatementAction_0_0(),
                                  current);
                          
                    }

                    }

                    otherlv_1=(Token)match(input,Warning,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getLintStatementAccess().getWarningKeyword_0_1());
                          
                    }
                    // InternalResoluteParser.g:4510:1: ( (lv_expr_2_0= ruleExpr ) )
                    // InternalResoluteParser.g:4511:1: (lv_expr_2_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:4511:1: (lv_expr_2_0= ruleExpr )
                    // InternalResoluteParser.g:4512:3: lv_expr_2_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getLintStatementAccess().getExprExprParserRuleCall_0_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_2_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getLintStatementRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_2_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:4529:6: ( () otherlv_4= Error ( (lv_expr_5_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:4529:6: ( () otherlv_4= Error ( (lv_expr_5_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:4529:7: () otherlv_4= Error ( (lv_expr_5_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:4529:7: ()
                    // InternalResoluteParser.g:4530:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getLintStatementAccess().getErrorStatementAction_1_0(),
                                  current);
                          
                    }

                    }

                    otherlv_4=(Token)match(input,Error,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getLintStatementAccess().getErrorKeyword_1_1());
                          
                    }
                    // InternalResoluteParser.g:4540:1: ( (lv_expr_5_0= ruleExpr ) )
                    // InternalResoluteParser.g:4541:1: (lv_expr_5_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:4541:1: (lv_expr_5_0= ruleExpr )
                    // InternalResoluteParser.g:4542:3: lv_expr_5_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getLintStatementAccess().getExprExprParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_5_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getLintStatementRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_5_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalResoluteParser.g:4559:6: ( () otherlv_7= Info ( (lv_expr_8_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:4559:6: ( () otherlv_7= Info ( (lv_expr_8_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:4559:7: () otherlv_7= Info ( (lv_expr_8_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:4559:7: ()
                    // InternalResoluteParser.g:4560:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getLintStatementAccess().getInfoStatementAction_2_0(),
                                  current);
                          
                    }

                    }

                    otherlv_7=(Token)match(input,Info,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_7, grammarAccess.getLintStatementAccess().getInfoKeyword_2_1());
                          
                    }
                    // InternalResoluteParser.g:4570:1: ( (lv_expr_8_0= ruleExpr ) )
                    // InternalResoluteParser.g:4571:1: (lv_expr_8_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:4571:1: (lv_expr_8_0= ruleExpr )
                    // InternalResoluteParser.g:4572:3: lv_expr_8_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getLintStatementAccess().getExprExprParserRuleCall_2_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_8_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getLintStatementRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_8_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLintStatement"


    // $ANTLR start "entryRuleRuleset"
    // InternalResoluteParser.g:4596:1: entryRuleRuleset returns [EObject current=null] : iv_ruleRuleset= ruleRuleset EOF ;
    public final EObject entryRuleRuleset() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRuleset = null;


        try {
            // InternalResoluteParser.g:4597:2: (iv_ruleRuleset= ruleRuleset EOF )
            // InternalResoluteParser.g:4598:2: iv_ruleRuleset= ruleRuleset EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRulesetRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleRuleset=ruleRuleset();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRuleset; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRuleset"


    // $ANTLR start "ruleRuleset"
    // InternalResoluteParser.g:4605:1: ruleRuleset returns [EObject current=null] : (otherlv_0= Ruleset ( (lv_name_1_0= RULE_ID ) ) otherlv_2= LeftCurlyBracket ( (lv_body_3_0= ruleRulesetBody ) ) otherlv_4= RightCurlyBracket ) ;
    public final EObject ruleRuleset() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_body_3_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4608:28: ( (otherlv_0= Ruleset ( (lv_name_1_0= RULE_ID ) ) otherlv_2= LeftCurlyBracket ( (lv_body_3_0= ruleRulesetBody ) ) otherlv_4= RightCurlyBracket ) )
            // InternalResoluteParser.g:4609:1: (otherlv_0= Ruleset ( (lv_name_1_0= RULE_ID ) ) otherlv_2= LeftCurlyBracket ( (lv_body_3_0= ruleRulesetBody ) ) otherlv_4= RightCurlyBracket )
            {
            // InternalResoluteParser.g:4609:1: (otherlv_0= Ruleset ( (lv_name_1_0= RULE_ID ) ) otherlv_2= LeftCurlyBracket ( (lv_body_3_0= ruleRulesetBody ) ) otherlv_4= RightCurlyBracket )
            // InternalResoluteParser.g:4610:2: otherlv_0= Ruleset ( (lv_name_1_0= RULE_ID ) ) otherlv_2= LeftCurlyBracket ( (lv_body_3_0= ruleRulesetBody ) ) otherlv_4= RightCurlyBracket
            {
            otherlv_0=(Token)match(input,Ruleset,FollowSets000.FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getRulesetAccess().getRulesetKeyword_0());
                  
            }
            // InternalResoluteParser.g:4614:1: ( (lv_name_1_0= RULE_ID ) )
            // InternalResoluteParser.g:4615:1: (lv_name_1_0= RULE_ID )
            {
            // InternalResoluteParser.g:4615:1: (lv_name_1_0= RULE_ID )
            // InternalResoluteParser.g:4616:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_50); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_1_0, grammarAccess.getRulesetAccess().getNameIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getRulesetRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_1_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.ID");
              	    
            }

            }


            }

            otherlv_2=(Token)match(input,LeftCurlyBracket,FollowSets000.FOLLOW_51); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getRulesetAccess().getLeftCurlyBracketKeyword_2());
                  
            }
            // InternalResoluteParser.g:4637:1: ( (lv_body_3_0= ruleRulesetBody ) )
            // InternalResoluteParser.g:4638:1: (lv_body_3_0= ruleRulesetBody )
            {
            // InternalResoluteParser.g:4638:1: (lv_body_3_0= ruleRulesetBody )
            // InternalResoluteParser.g:4639:3: lv_body_3_0= ruleRulesetBody
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getRulesetAccess().getBodyRulesetBodyParserRuleCall_3_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_6);
            lv_body_3_0=ruleRulesetBody();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getRulesetRule());
              	        }
                     		set(
                     			current, 
                     			"body",
                      		lv_body_3_0, 
                      		"com.rockwellcollins.atc.resolute.Resolute.RulesetBody");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            otherlv_4=(Token)match(input,RightCurlyBracket,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_4, grammarAccess.getRulesetAccess().getRightCurlyBracketKeyword_4());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRuleset"


    // $ANTLR start "entryRuleRulesetBody"
    // InternalResoluteParser.g:4668:1: entryRuleRulesetBody returns [EObject current=null] : iv_ruleRulesetBody= ruleRulesetBody EOF ;
    public final EObject entryRuleRulesetBody() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRulesetBody = null;


        try {
            // InternalResoluteParser.g:4669:2: (iv_ruleRulesetBody= ruleRulesetBody EOF )
            // InternalResoluteParser.g:4670:2: iv_ruleRulesetBody= ruleRulesetBody EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRulesetBodyRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleRulesetBody=ruleRulesetBody();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRulesetBody; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRulesetBody"


    // $ANTLR start "ruleRulesetBody"
    // InternalResoluteParser.g:4677:1: ruleRulesetBody returns [EObject current=null] : ( () ( (lv_lintStatements_1_0= ruleLintStatement ) )* ) ;
    public final EObject ruleRulesetBody() throws RecognitionException {
        EObject current = null;

        EObject lv_lintStatements_1_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4680:28: ( ( () ( (lv_lintStatements_1_0= ruleLintStatement ) )* ) )
            // InternalResoluteParser.g:4681:1: ( () ( (lv_lintStatements_1_0= ruleLintStatement ) )* )
            {
            // InternalResoluteParser.g:4681:1: ( () ( (lv_lintStatements_1_0= ruleLintStatement ) )* )
            // InternalResoluteParser.g:4681:2: () ( (lv_lintStatements_1_0= ruleLintStatement ) )*
            {
            // InternalResoluteParser.g:4681:2: ()
            // InternalResoluteParser.g:4682:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getRulesetBodyAccess().getRulesetBodyAction_0(),
                          current);
                  
            }

            }

            // InternalResoluteParser.g:4687:2: ( (lv_lintStatements_1_0= ruleLintStatement ) )*
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==Warning||LA56_0==Error||LA56_0==Info) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // InternalResoluteParser.g:4688:1: (lv_lintStatements_1_0= ruleLintStatement )
            	    {
            	    // InternalResoluteParser.g:4688:1: (lv_lintStatements_1_0= ruleLintStatement )
            	    // InternalResoluteParser.g:4689:3: lv_lintStatements_1_0= ruleLintStatement
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getRulesetBodyAccess().getLintStatementsLintStatementParserRuleCall_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_52);
            	    lv_lintStatements_1_0=ruleLintStatement();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getRulesetBodyRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"lintStatements",
            	              		lv_lintStatements_1_0, 
            	              		"com.rockwellcollins.atc.resolute.Resolute.LintStatement");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop56;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRulesetBody"


    // $ANTLR start "entryRuleAnalysisStatement"
    // InternalResoluteParser.g:4713:1: entryRuleAnalysisStatement returns [EObject current=null] : iv_ruleAnalysisStatement= ruleAnalysisStatement EOF ;
    public final EObject entryRuleAnalysisStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAnalysisStatement = null;


        try {
            // InternalResoluteParser.g:4714:2: (iv_ruleAnalysisStatement= ruleAnalysisStatement EOF )
            // InternalResoluteParser.g:4715:2: iv_ruleAnalysisStatement= ruleAnalysisStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAnalysisStatementRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAnalysisStatement=ruleAnalysisStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAnalysisStatement; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAnalysisStatement"


    // $ANTLR start "ruleAnalysisStatement"
    // InternalResoluteParser.g:4722:1: ruleAnalysisStatement returns [EObject current=null] : ( ( () otherlv_1= Prove ( (lv_expr_2_0= ruleExpr ) ) ) | ( () otherlv_4= Check ( (lv_expr_5_0= ruleExpr ) ) ) ) ;
    public final EObject ruleAnalysisStatement() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_4=null;
        EObject lv_expr_2_0 = null;

        EObject lv_expr_5_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4725:28: ( ( ( () otherlv_1= Prove ( (lv_expr_2_0= ruleExpr ) ) ) | ( () otherlv_4= Check ( (lv_expr_5_0= ruleExpr ) ) ) ) )
            // InternalResoluteParser.g:4726:1: ( ( () otherlv_1= Prove ( (lv_expr_2_0= ruleExpr ) ) ) | ( () otherlv_4= Check ( (lv_expr_5_0= ruleExpr ) ) ) )
            {
            // InternalResoluteParser.g:4726:1: ( ( () otherlv_1= Prove ( (lv_expr_2_0= ruleExpr ) ) ) | ( () otherlv_4= Check ( (lv_expr_5_0= ruleExpr ) ) ) )
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==Prove) ) {
                alt57=1;
            }
            else if ( (LA57_0==Check) ) {
                alt57=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;
            }
            switch (alt57) {
                case 1 :
                    // InternalResoluteParser.g:4726:2: ( () otherlv_1= Prove ( (lv_expr_2_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:4726:2: ( () otherlv_1= Prove ( (lv_expr_2_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:4726:3: () otherlv_1= Prove ( (lv_expr_2_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:4726:3: ()
                    // InternalResoluteParser.g:4727:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAnalysisStatementAccess().getProveStatementAction_0_0(),
                                  current);
                          
                    }

                    }

                    otherlv_1=(Token)match(input,Prove,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getAnalysisStatementAccess().getProveKeyword_0_1());
                          
                    }
                    // InternalResoluteParser.g:4737:1: ( (lv_expr_2_0= ruleExpr ) )
                    // InternalResoluteParser.g:4738:1: (lv_expr_2_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:4738:1: (lv_expr_2_0= ruleExpr )
                    // InternalResoluteParser.g:4739:3: lv_expr_2_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAnalysisStatementAccess().getExprExprParserRuleCall_0_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_2_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAnalysisStatementRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_2_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:4756:6: ( () otherlv_4= Check ( (lv_expr_5_0= ruleExpr ) ) )
                    {
                    // InternalResoluteParser.g:4756:6: ( () otherlv_4= Check ( (lv_expr_5_0= ruleExpr ) ) )
                    // InternalResoluteParser.g:4756:7: () otherlv_4= Check ( (lv_expr_5_0= ruleExpr ) )
                    {
                    // InternalResoluteParser.g:4756:7: ()
                    // InternalResoluteParser.g:4757:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getAnalysisStatementAccess().getCheckStatementAction_1_0(),
                                  current);
                          
                    }

                    }

                    otherlv_4=(Token)match(input,Check,FollowSets000.FOLLOW_12); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getAnalysisStatementAccess().getCheckKeyword_1_1());
                          
                    }
                    // InternalResoluteParser.g:4767:1: ( (lv_expr_5_0= ruleExpr ) )
                    // InternalResoluteParser.g:4768:1: (lv_expr_5_0= ruleExpr )
                    {
                    // InternalResoluteParser.g:4768:1: (lv_expr_5_0= ruleExpr )
                    // InternalResoluteParser.g:4769:3: lv_expr_5_0= ruleExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getAnalysisStatementAccess().getExprExprParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expr_5_0=ruleExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getAnalysisStatementRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_5_0, 
                              		"com.rockwellcollins.atc.resolute.Resolute.Expr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAnalysisStatement"


    // $ANTLR start "entryRuleContainedPropertyAssociation"
    // InternalResoluteParser.g:4795:1: entryRuleContainedPropertyAssociation returns [EObject current=null] : iv_ruleContainedPropertyAssociation= ruleContainedPropertyAssociation EOF ;
    public final EObject entryRuleContainedPropertyAssociation() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleContainedPropertyAssociation = null;


        try {
            // InternalResoluteParser.g:4796:2: (iv_ruleContainedPropertyAssociation= ruleContainedPropertyAssociation EOF )
            // InternalResoluteParser.g:4797:2: iv_ruleContainedPropertyAssociation= ruleContainedPropertyAssociation EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getContainedPropertyAssociationRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleContainedPropertyAssociation=ruleContainedPropertyAssociation();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleContainedPropertyAssociation; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleContainedPropertyAssociation"


    // $ANTLR start "ruleContainedPropertyAssociation"
    // InternalResoluteParser.g:4804:1: ruleContainedPropertyAssociation returns [EObject current=null] : ( ( ( ruleQPREF ) ) (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) ) ( (lv_constant_3_0= Constant ) )? ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* ) ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )? ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )? otherlv_15= Semicolon ) ;
    public final EObject ruleContainedPropertyAssociation() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token lv_append_2_0=null;
        Token lv_constant_3_0=null;
        Token otherlv_5=null;
        Token otherlv_9=null;
        Token otherlv_12=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        EObject lv_ownedValue_4_0 = null;

        EObject lv_ownedValue_6_0 = null;

        EObject lv_appliesTo_8_0 = null;

        EObject lv_appliesTo_10_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:4807:28: ( ( ( ( ruleQPREF ) ) (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) ) ( (lv_constant_3_0= Constant ) )? ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* ) ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )? ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )? otherlv_15= Semicolon ) )
            // InternalResoluteParser.g:4808:1: ( ( ( ruleQPREF ) ) (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) ) ( (lv_constant_3_0= Constant ) )? ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* ) ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )? ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )? otherlv_15= Semicolon )
            {
            // InternalResoluteParser.g:4808:1: ( ( ( ruleQPREF ) ) (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) ) ( (lv_constant_3_0= Constant ) )? ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* ) ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )? ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )? otherlv_15= Semicolon )
            // InternalResoluteParser.g:4808:2: ( ( ruleQPREF ) ) (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) ) ( (lv_constant_3_0= Constant ) )? ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* ) ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )? ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )? otherlv_15= Semicolon
            {
            // InternalResoluteParser.g:4808:2: ( ( ruleQPREF ) )
            // InternalResoluteParser.g:4809:1: ( ruleQPREF )
            {
            // InternalResoluteParser.g:4809:1: ( ruleQPREF )
            // InternalResoluteParser.g:4810:3: ruleQPREF
            {
            if ( state.backtracking==0 ) {

              			if (current==null) {
              	            current = createModelElement(grammarAccess.getContainedPropertyAssociationRule());
              	        }
                      
            }
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getPropertyPropertyCrossReference_0_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_53);
            ruleQPREF();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // InternalResoluteParser.g:4824:2: (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) )
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==EqualsSignGreaterThanSign) ) {
                alt58=1;
            }
            else if ( (LA58_0==PlusSignEqualsSignGreaterThanSign) ) {
                alt58=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 58, 0, input);

                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // InternalResoluteParser.g:4825:2: otherlv_1= EqualsSignGreaterThanSign
                    {
                    otherlv_1=(Token)match(input,EqualsSignGreaterThanSign,FollowSets000.FOLLOW_54); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getContainedPropertyAssociationAccess().getEqualsSignGreaterThanSignKeyword_1_0());
                          
                    }

                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:4830:6: ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) )
                    {
                    // InternalResoluteParser.g:4830:6: ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) )
                    // InternalResoluteParser.g:4831:1: (lv_append_2_0= PlusSignEqualsSignGreaterThanSign )
                    {
                    // InternalResoluteParser.g:4831:1: (lv_append_2_0= PlusSignEqualsSignGreaterThanSign )
                    // InternalResoluteParser.g:4832:3: lv_append_2_0= PlusSignEqualsSignGreaterThanSign
                    {
                    lv_append_2_0=(Token)match(input,PlusSignEqualsSignGreaterThanSign,FollowSets000.FOLLOW_54); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_append_2_0, grammarAccess.getContainedPropertyAssociationAccess().getAppendPlusSignEqualsSignGreaterThanSignKeyword_1_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getContainedPropertyAssociationRule());
                      	        }
                             		setWithLastConsumed(current, "append", true, "+=>");
                      	    
                    }

                    }


                    }


                    }
                    break;

            }

            // InternalResoluteParser.g:4846:3: ( (lv_constant_3_0= Constant ) )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==Constant) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // InternalResoluteParser.g:4847:1: (lv_constant_3_0= Constant )
                    {
                    // InternalResoluteParser.g:4847:1: (lv_constant_3_0= Constant )
                    // InternalResoluteParser.g:4848:3: lv_constant_3_0= Constant
                    {
                    lv_constant_3_0=(Token)match(input,Constant,FollowSets000.FOLLOW_54); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_constant_3_0, grammarAccess.getContainedPropertyAssociationAccess().getConstantConstantKeyword_2_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getContainedPropertyAssociationRule());
                      	        }
                             		setWithLastConsumed(current, "constant", true, "constant");
                      	    
                    }

                    }


                    }
                    break;

            }

            // InternalResoluteParser.g:4862:3: ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* )
            // InternalResoluteParser.g:4862:4: ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )*
            {
            // InternalResoluteParser.g:4862:4: ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) )
            // InternalResoluteParser.g:4863:1: (lv_ownedValue_4_0= ruleOptionalModalPropertyValue )
            {
            // InternalResoluteParser.g:4863:1: (lv_ownedValue_4_0= ruleOptionalModalPropertyValue )
            // InternalResoluteParser.g:4864:3: lv_ownedValue_4_0= ruleOptionalModalPropertyValue
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getOwnedValueOptionalModalPropertyValueParserRuleCall_3_0_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_55);
            lv_ownedValue_4_0=ruleOptionalModalPropertyValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getContainedPropertyAssociationRule());
              	        }
                     		add(
                     			current, 
                     			"ownedValue",
                      		lv_ownedValue_4_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.OptionalModalPropertyValue");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // InternalResoluteParser.g:4880:2: (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==Comma) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // InternalResoluteParser.g:4881:2: otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) )
            	    {
            	    otherlv_5=(Token)match(input,Comma,FollowSets000.FOLLOW_54); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	          	newLeafNode(otherlv_5, grammarAccess.getContainedPropertyAssociationAccess().getCommaKeyword_3_1_0());
            	          
            	    }
            	    // InternalResoluteParser.g:4885:1: ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) )
            	    // InternalResoluteParser.g:4886:1: (lv_ownedValue_6_0= ruleOptionalModalPropertyValue )
            	    {
            	    // InternalResoluteParser.g:4886:1: (lv_ownedValue_6_0= ruleOptionalModalPropertyValue )
            	    // InternalResoluteParser.g:4887:3: lv_ownedValue_6_0= ruleOptionalModalPropertyValue
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getOwnedValueOptionalModalPropertyValueParserRuleCall_3_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_55);
            	    lv_ownedValue_6_0=ruleOptionalModalPropertyValue();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getContainedPropertyAssociationRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"ownedValue",
            	              		lv_ownedValue_6_0, 
            	              		"org.osate.xtext.aadl2.properties.Properties.OptionalModalPropertyValue");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop60;
                }
            } while (true);


            }

            // InternalResoluteParser.g:4903:5: ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==Applies) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // InternalResoluteParser.g:4904:5: ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )*
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getAppliesToKeywordsParserRuleCall_4_0()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_10);
                    ruleAppliesToKeywords();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              afterParserOrEnumRuleCall();
                          
                    }
                    // InternalResoluteParser.g:4911:1: ( (lv_appliesTo_8_0= ruleContainmentPath ) )
                    // InternalResoluteParser.g:4912:1: (lv_appliesTo_8_0= ruleContainmentPath )
                    {
                    // InternalResoluteParser.g:4912:1: (lv_appliesTo_8_0= ruleContainmentPath )
                    // InternalResoluteParser.g:4913:3: lv_appliesTo_8_0= ruleContainmentPath
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getAppliesToContainmentPathParserRuleCall_4_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_56);
                    lv_appliesTo_8_0=ruleContainmentPath();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getContainedPropertyAssociationRule());
                      	        }
                             		add(
                             			current, 
                             			"appliesTo",
                              		lv_appliesTo_8_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.ContainmentPath");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // InternalResoluteParser.g:4929:2: (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )*
                    loop61:
                    do {
                        int alt61=2;
                        int LA61_0 = input.LA(1);

                        if ( (LA61_0==Comma) ) {
                            alt61=1;
                        }


                        switch (alt61) {
                    	case 1 :
                    	    // InternalResoluteParser.g:4930:2: otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) )
                    	    {
                    	    otherlv_9=(Token)match(input,Comma,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	          	newLeafNode(otherlv_9, grammarAccess.getContainedPropertyAssociationAccess().getCommaKeyword_4_2_0());
                    	          
                    	    }
                    	    // InternalResoluteParser.g:4934:1: ( (lv_appliesTo_10_0= ruleContainmentPath ) )
                    	    // InternalResoluteParser.g:4935:1: (lv_appliesTo_10_0= ruleContainmentPath )
                    	    {
                    	    // InternalResoluteParser.g:4935:1: (lv_appliesTo_10_0= ruleContainmentPath )
                    	    // InternalResoluteParser.g:4936:3: lv_appliesTo_10_0= ruleContainmentPath
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getAppliesToContainmentPathParserRuleCall_4_2_1_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_56);
                    	    lv_appliesTo_10_0=ruleContainmentPath();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getContainedPropertyAssociationRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"appliesTo",
                    	              		lv_appliesTo_10_0, 
                    	              		"org.osate.xtext.aadl2.properties.Properties.ContainmentPath");
                    	      	        afterParserOrEnumRuleCall();
                    	      	    
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop61;
                        }
                    } while (true);


                    }
                    break;

            }

            // InternalResoluteParser.g:4952:6: ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==In) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // InternalResoluteParser.g:4953:5: ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getInBindingKeywordsParserRuleCall_5_0()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_14);
                    ruleInBindingKeywords();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              afterParserOrEnumRuleCall();
                          
                    }
                    otherlv_12=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_12, grammarAccess.getContainedPropertyAssociationAccess().getLeftParenthesisKeyword_5_1());
                          
                    }
                    // InternalResoluteParser.g:4965:1: ( ( ruleQCREF ) )
                    // InternalResoluteParser.g:4966:1: ( ruleQCREF )
                    {
                    // InternalResoluteParser.g:4966:1: ( ruleQCREF )
                    // InternalResoluteParser.g:4967:3: ruleQCREF
                    {
                    if ( state.backtracking==0 ) {

                      			if (current==null) {
                      	            current = createModelElement(grammarAccess.getContainedPropertyAssociationRule());
                      	        }
                              
                    }
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getInBindingClassifierCrossReference_5_2_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_34);
                    ruleQCREF();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_14=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_47); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_14, grammarAccess.getContainedPropertyAssociationAccess().getRightParenthesisKeyword_5_3());
                          
                    }

                    }
                    break;

            }

            otherlv_15=(Token)match(input,Semicolon,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_15, grammarAccess.getContainedPropertyAssociationAccess().getSemicolonKeyword_6());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleContainedPropertyAssociation"


    // $ANTLR start "entryRuleContainmentPath"
    // InternalResoluteParser.g:5003:1: entryRuleContainmentPath returns [EObject current=null] : iv_ruleContainmentPath= ruleContainmentPath EOF ;
    public final EObject entryRuleContainmentPath() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleContainmentPath = null;


        try {
            // InternalResoluteParser.g:5004:2: (iv_ruleContainmentPath= ruleContainmentPath EOF )
            // InternalResoluteParser.g:5005:2: iv_ruleContainmentPath= ruleContainmentPath EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getContainmentPathRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleContainmentPath=ruleContainmentPath();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleContainmentPath; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleContainmentPath"


    // $ANTLR start "ruleContainmentPath"
    // InternalResoluteParser.g:5012:1: ruleContainmentPath returns [EObject current=null] : ( (lv_path_0_0= ruleContainmentPathElement ) ) ;
    public final EObject ruleContainmentPath() throws RecognitionException {
        EObject current = null;

        EObject lv_path_0_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5015:28: ( ( (lv_path_0_0= ruleContainmentPathElement ) ) )
            // InternalResoluteParser.g:5016:1: ( (lv_path_0_0= ruleContainmentPathElement ) )
            {
            // InternalResoluteParser.g:5016:1: ( (lv_path_0_0= ruleContainmentPathElement ) )
            // InternalResoluteParser.g:5017:1: (lv_path_0_0= ruleContainmentPathElement )
            {
            // InternalResoluteParser.g:5017:1: (lv_path_0_0= ruleContainmentPathElement )
            // InternalResoluteParser.g:5018:3: lv_path_0_0= ruleContainmentPathElement
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getContainmentPathAccess().getPathContainmentPathElementParserRuleCall_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_path_0_0=ruleContainmentPathElement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getContainmentPathRule());
              	        }
                     		set(
                     			current, 
                     			"path",
                      		lv_path_0_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.ContainmentPathElement");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleContainmentPath"


    // $ANTLR start "entryRuleOptionalModalPropertyValue"
    // InternalResoluteParser.g:5044:1: entryRuleOptionalModalPropertyValue returns [EObject current=null] : iv_ruleOptionalModalPropertyValue= ruleOptionalModalPropertyValue EOF ;
    public final EObject entryRuleOptionalModalPropertyValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOptionalModalPropertyValue = null;


        try {
            // InternalResoluteParser.g:5045:2: (iv_ruleOptionalModalPropertyValue= ruleOptionalModalPropertyValue EOF )
            // InternalResoluteParser.g:5046:2: iv_ruleOptionalModalPropertyValue= ruleOptionalModalPropertyValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOptionalModalPropertyValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleOptionalModalPropertyValue=ruleOptionalModalPropertyValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOptionalModalPropertyValue; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOptionalModalPropertyValue"


    // $ANTLR start "ruleOptionalModalPropertyValue"
    // InternalResoluteParser.g:5053:1: ruleOptionalModalPropertyValue returns [EObject current=null] : ( ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )? ) ;
    public final EObject ruleOptionalModalPropertyValue() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        EObject lv_ownedValue_0_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5056:28: ( ( ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )? ) )
            // InternalResoluteParser.g:5057:1: ( ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )? )
            {
            // InternalResoluteParser.g:5057:1: ( ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )? )
            // InternalResoluteParser.g:5057:2: ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )?
            {
            // InternalResoluteParser.g:5057:2: ( (lv_ownedValue_0_0= rulePropertyExpression ) )
            // InternalResoluteParser.g:5058:1: (lv_ownedValue_0_0= rulePropertyExpression )
            {
            // InternalResoluteParser.g:5058:1: (lv_ownedValue_0_0= rulePropertyExpression )
            // InternalResoluteParser.g:5059:3: lv_ownedValue_0_0= rulePropertyExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getOptionalModalPropertyValueAccess().getOwnedValuePropertyExpressionParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_57);
            lv_ownedValue_0_0=rulePropertyExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getOptionalModalPropertyValueRule());
              	        }
                     		set(
                     			current, 
                     			"ownedValue",
                      		lv_ownedValue_0_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.PropertyExpression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // InternalResoluteParser.g:5075:2: ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==In) ) {
                int LA65_1 = input.LA(2);

                if ( (LA65_1==Modes) ) {
                    alt65=1;
                }
            }
            switch (alt65) {
                case 1 :
                    // InternalResoluteParser.g:5076:5: ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getOptionalModalPropertyValueAccess().getInModesKeywordsParserRuleCall_1_0()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_14);
                    ruleInModesKeywords();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              afterParserOrEnumRuleCall();
                          
                    }
                    otherlv_2=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_2, grammarAccess.getOptionalModalPropertyValueAccess().getLeftParenthesisKeyword_1_1());
                          
                    }
                    // InternalResoluteParser.g:5088:1: ( (otherlv_3= RULE_ID ) )
                    // InternalResoluteParser.g:5089:1: (otherlv_3= RULE_ID )
                    {
                    // InternalResoluteParser.g:5089:1: (otherlv_3= RULE_ID )
                    // InternalResoluteParser.g:5090:3: otherlv_3= RULE_ID
                    {
                    if ( state.backtracking==0 ) {

                      			if (current==null) {
                      	            current = createModelElement(grammarAccess.getOptionalModalPropertyValueRule());
                      	        }
                              
                    }
                    otherlv_3=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_16); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		newLeafNode(otherlv_3, grammarAccess.getOptionalModalPropertyValueAccess().getInModeModeCrossReference_1_2_0()); 
                      	
                    }

                    }


                    }

                    // InternalResoluteParser.g:5101:2: (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )*
                    loop64:
                    do {
                        int alt64=2;
                        int LA64_0 = input.LA(1);

                        if ( (LA64_0==Comma) ) {
                            alt64=1;
                        }


                        switch (alt64) {
                    	case 1 :
                    	    // InternalResoluteParser.g:5102:2: otherlv_4= Comma ( (otherlv_5= RULE_ID ) )
                    	    {
                    	    otherlv_4=(Token)match(input,Comma,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	          	newLeafNode(otherlv_4, grammarAccess.getOptionalModalPropertyValueAccess().getCommaKeyword_1_3_0());
                    	          
                    	    }
                    	    // InternalResoluteParser.g:5106:1: ( (otherlv_5= RULE_ID ) )
                    	    // InternalResoluteParser.g:5107:1: (otherlv_5= RULE_ID )
                    	    {
                    	    // InternalResoluteParser.g:5107:1: (otherlv_5= RULE_ID )
                    	    // InternalResoluteParser.g:5108:3: otherlv_5= RULE_ID
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      			if (current==null) {
                    	      	            current = createModelElement(grammarAccess.getOptionalModalPropertyValueRule());
                    	      	        }
                    	              
                    	    }
                    	    otherlv_5=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_16); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      		newLeafNode(otherlv_5, grammarAccess.getOptionalModalPropertyValueAccess().getInModeModeCrossReference_1_3_1_0()); 
                    	      	
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop64;
                        }
                    } while (true);

                    otherlv_6=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_6, grammarAccess.getOptionalModalPropertyValueAccess().getRightParenthesisKeyword_1_4());
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOptionalModalPropertyValue"


    // $ANTLR start "entryRulePropertyValue"
    // InternalResoluteParser.g:5132:1: entryRulePropertyValue returns [EObject current=null] : iv_rulePropertyValue= rulePropertyValue EOF ;
    public final EObject entryRulePropertyValue() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePropertyValue = null;


        try {
            // InternalResoluteParser.g:5133:2: (iv_rulePropertyValue= rulePropertyValue EOF )
            // InternalResoluteParser.g:5134:2: iv_rulePropertyValue= rulePropertyValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPropertyValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_rulePropertyValue=rulePropertyValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePropertyValue; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePropertyValue"


    // $ANTLR start "rulePropertyValue"
    // InternalResoluteParser.g:5141:1: rulePropertyValue returns [EObject current=null] : ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ;
    public final EObject rulePropertyValue() throws RecognitionException {
        EObject current = null;

        EObject lv_ownedValue_0_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5144:28: ( ( (lv_ownedValue_0_0= rulePropertyExpression ) ) )
            // InternalResoluteParser.g:5145:1: ( (lv_ownedValue_0_0= rulePropertyExpression ) )
            {
            // InternalResoluteParser.g:5145:1: ( (lv_ownedValue_0_0= rulePropertyExpression ) )
            // InternalResoluteParser.g:5146:1: (lv_ownedValue_0_0= rulePropertyExpression )
            {
            // InternalResoluteParser.g:5146:1: (lv_ownedValue_0_0= rulePropertyExpression )
            // InternalResoluteParser.g:5147:3: lv_ownedValue_0_0= rulePropertyExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getPropertyValueAccess().getOwnedValuePropertyExpressionParserRuleCall_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_ownedValue_0_0=rulePropertyExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getPropertyValueRule());
              	        }
                     		set(
                     			current, 
                     			"ownedValue",
                      		lv_ownedValue_0_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.PropertyExpression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePropertyValue"


    // $ANTLR start "entryRulePropertyExpression"
    // InternalResoluteParser.g:5171:1: entryRulePropertyExpression returns [EObject current=null] : iv_rulePropertyExpression= rulePropertyExpression EOF ;
    public final EObject entryRulePropertyExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePropertyExpression = null;


        try {
            // InternalResoluteParser.g:5172:2: (iv_rulePropertyExpression= rulePropertyExpression EOF )
            // InternalResoluteParser.g:5173:2: iv_rulePropertyExpression= rulePropertyExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPropertyExpressionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_rulePropertyExpression=rulePropertyExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePropertyExpression; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePropertyExpression"


    // $ANTLR start "rulePropertyExpression"
    // InternalResoluteParser.g:5180:1: rulePropertyExpression returns [EObject current=null] : (this_RecordTerm_0= ruleRecordTerm | this_ReferenceTerm_1= ruleReferenceTerm | this_ComponentClassifierTerm_2= ruleComponentClassifierTerm | this_ComputedTerm_3= ruleComputedTerm | this_StringTerm_4= ruleStringTerm | this_NumericRangeTerm_5= ruleNumericRangeTerm | this_RealTerm_6= ruleRealTerm | this_IntegerTerm_7= ruleIntegerTerm | this_ListTerm_8= ruleListTerm | this_BooleanLiteral_9= ruleBooleanLiteral | this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm ) ;
    public final EObject rulePropertyExpression() throws RecognitionException {
        EObject current = null;

        EObject this_RecordTerm_0 = null;

        EObject this_ReferenceTerm_1 = null;

        EObject this_ComponentClassifierTerm_2 = null;

        EObject this_ComputedTerm_3 = null;

        EObject this_StringTerm_4 = null;

        EObject this_NumericRangeTerm_5 = null;

        EObject this_RealTerm_6 = null;

        EObject this_IntegerTerm_7 = null;

        EObject this_ListTerm_8 = null;

        EObject this_BooleanLiteral_9 = null;

        EObject this_LiteralorReferenceTerm_10 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5183:28: ( (this_RecordTerm_0= ruleRecordTerm | this_ReferenceTerm_1= ruleReferenceTerm | this_ComponentClassifierTerm_2= ruleComponentClassifierTerm | this_ComputedTerm_3= ruleComputedTerm | this_StringTerm_4= ruleStringTerm | this_NumericRangeTerm_5= ruleNumericRangeTerm | this_RealTerm_6= ruleRealTerm | this_IntegerTerm_7= ruleIntegerTerm | this_ListTerm_8= ruleListTerm | this_BooleanLiteral_9= ruleBooleanLiteral | this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm ) )
            // InternalResoluteParser.g:5184:1: (this_RecordTerm_0= ruleRecordTerm | this_ReferenceTerm_1= ruleReferenceTerm | this_ComponentClassifierTerm_2= ruleComponentClassifierTerm | this_ComputedTerm_3= ruleComputedTerm | this_StringTerm_4= ruleStringTerm | this_NumericRangeTerm_5= ruleNumericRangeTerm | this_RealTerm_6= ruleRealTerm | this_IntegerTerm_7= ruleIntegerTerm | this_ListTerm_8= ruleListTerm | this_BooleanLiteral_9= ruleBooleanLiteral | this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm )
            {
            // InternalResoluteParser.g:5184:1: (this_RecordTerm_0= ruleRecordTerm | this_ReferenceTerm_1= ruleReferenceTerm | this_ComponentClassifierTerm_2= ruleComponentClassifierTerm | this_ComputedTerm_3= ruleComputedTerm | this_StringTerm_4= ruleStringTerm | this_NumericRangeTerm_5= ruleNumericRangeTerm | this_RealTerm_6= ruleRealTerm | this_IntegerTerm_7= ruleIntegerTerm | this_ListTerm_8= ruleListTerm | this_BooleanLiteral_9= ruleBooleanLiteral | this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm )
            int alt66=11;
            alt66 = dfa66.predict(input);
            switch (alt66) {
                case 1 :
                    // InternalResoluteParser.g:5185:5: this_RecordTerm_0= ruleRecordTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getRecordTermParserRuleCall_0()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_RecordTerm_0=ruleRecordTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_RecordTerm_0;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:5195:5: this_ReferenceTerm_1= ruleReferenceTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getReferenceTermParserRuleCall_1()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ReferenceTerm_1=ruleReferenceTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_ReferenceTerm_1;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // InternalResoluteParser.g:5205:5: this_ComponentClassifierTerm_2= ruleComponentClassifierTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getComponentClassifierTermParserRuleCall_2()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ComponentClassifierTerm_2=ruleComponentClassifierTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_ComponentClassifierTerm_2;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 4 :
                    // InternalResoluteParser.g:5215:5: this_ComputedTerm_3= ruleComputedTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getComputedTermParserRuleCall_3()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ComputedTerm_3=ruleComputedTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_ComputedTerm_3;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 5 :
                    // InternalResoluteParser.g:5225:5: this_StringTerm_4= ruleStringTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getStringTermParserRuleCall_4()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_StringTerm_4=ruleStringTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_StringTerm_4;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 6 :
                    // InternalResoluteParser.g:5235:5: this_NumericRangeTerm_5= ruleNumericRangeTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getNumericRangeTermParserRuleCall_5()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_NumericRangeTerm_5=ruleNumericRangeTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_NumericRangeTerm_5;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 7 :
                    // InternalResoluteParser.g:5245:5: this_RealTerm_6= ruleRealTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getRealTermParserRuleCall_6()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_RealTerm_6=ruleRealTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_RealTerm_6;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 8 :
                    // InternalResoluteParser.g:5255:5: this_IntegerTerm_7= ruleIntegerTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getIntegerTermParserRuleCall_7()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerTerm_7=ruleIntegerTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_IntegerTerm_7;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 9 :
                    // InternalResoluteParser.g:5265:5: this_ListTerm_8= ruleListTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getListTermParserRuleCall_8()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ListTerm_8=ruleListTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_ListTerm_8;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 10 :
                    // InternalResoluteParser.g:5275:5: this_BooleanLiteral_9= ruleBooleanLiteral
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getBooleanLiteralParserRuleCall_9()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_BooleanLiteral_9=ruleBooleanLiteral();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_BooleanLiteral_9;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 11 :
                    // InternalResoluteParser.g:5285:5: this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPropertyExpressionAccess().getLiteralorReferenceTermParserRuleCall_10()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_LiteralorReferenceTerm_10=ruleLiteralorReferenceTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_LiteralorReferenceTerm_10;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePropertyExpression"


    // $ANTLR start "entryRuleLiteralorReferenceTerm"
    // InternalResoluteParser.g:5301:1: entryRuleLiteralorReferenceTerm returns [EObject current=null] : iv_ruleLiteralorReferenceTerm= ruleLiteralorReferenceTerm EOF ;
    public final EObject entryRuleLiteralorReferenceTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLiteralorReferenceTerm = null;


        try {
            // InternalResoluteParser.g:5302:2: (iv_ruleLiteralorReferenceTerm= ruleLiteralorReferenceTerm EOF )
            // InternalResoluteParser.g:5303:2: iv_ruleLiteralorReferenceTerm= ruleLiteralorReferenceTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getLiteralorReferenceTermRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleLiteralorReferenceTerm=ruleLiteralorReferenceTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleLiteralorReferenceTerm; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLiteralorReferenceTerm"


    // $ANTLR start "ruleLiteralorReferenceTerm"
    // InternalResoluteParser.g:5310:1: ruleLiteralorReferenceTerm returns [EObject current=null] : ( ( ruleQPREF ) ) ;
    public final EObject ruleLiteralorReferenceTerm() throws RecognitionException {
        EObject current = null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5313:28: ( ( ( ruleQPREF ) ) )
            // InternalResoluteParser.g:5314:1: ( ( ruleQPREF ) )
            {
            // InternalResoluteParser.g:5314:1: ( ( ruleQPREF ) )
            // InternalResoluteParser.g:5315:1: ( ruleQPREF )
            {
            // InternalResoluteParser.g:5315:1: ( ruleQPREF )
            // InternalResoluteParser.g:5316:3: ruleQPREF
            {
            if ( state.backtracking==0 ) {

              			if (current==null) {
              	            current = createModelElement(grammarAccess.getLiteralorReferenceTermRule());
              	        }
                      
            }
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getLiteralorReferenceTermAccess().getNamedValueAbstractNamedValueCrossReference_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            ruleQPREF();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLiteralorReferenceTerm"


    // $ANTLR start "entryRuleBooleanLiteral"
    // InternalResoluteParser.g:5338:1: entryRuleBooleanLiteral returns [EObject current=null] : iv_ruleBooleanLiteral= ruleBooleanLiteral EOF ;
    public final EObject entryRuleBooleanLiteral() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBooleanLiteral = null;


        try {
            // InternalResoluteParser.g:5339:2: (iv_ruleBooleanLiteral= ruleBooleanLiteral EOF )
            // InternalResoluteParser.g:5340:2: iv_ruleBooleanLiteral= ruleBooleanLiteral EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBooleanLiteralRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleBooleanLiteral=ruleBooleanLiteral();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBooleanLiteral; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBooleanLiteral"


    // $ANTLR start "ruleBooleanLiteral"
    // InternalResoluteParser.g:5347:1: ruleBooleanLiteral returns [EObject current=null] : ( () ( ( (lv_value_1_0= True ) ) | otherlv_2= False ) ) ;
    public final EObject ruleBooleanLiteral() throws RecognitionException {
        EObject current = null;

        Token lv_value_1_0=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5350:28: ( ( () ( ( (lv_value_1_0= True ) ) | otherlv_2= False ) ) )
            // InternalResoluteParser.g:5351:1: ( () ( ( (lv_value_1_0= True ) ) | otherlv_2= False ) )
            {
            // InternalResoluteParser.g:5351:1: ( () ( ( (lv_value_1_0= True ) ) | otherlv_2= False ) )
            // InternalResoluteParser.g:5351:2: () ( ( (lv_value_1_0= True ) ) | otherlv_2= False )
            {
            // InternalResoluteParser.g:5351:2: ()
            // InternalResoluteParser.g:5352:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getBooleanLiteralAccess().getBooleanLiteralAction_0(),
                          current);
                  
            }

            }

            // InternalResoluteParser.g:5357:2: ( ( (lv_value_1_0= True ) ) | otherlv_2= False )
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==True) ) {
                alt67=1;
            }
            else if ( (LA67_0==False) ) {
                alt67=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 67, 0, input);

                throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // InternalResoluteParser.g:5357:3: ( (lv_value_1_0= True ) )
                    {
                    // InternalResoluteParser.g:5357:3: ( (lv_value_1_0= True ) )
                    // InternalResoluteParser.g:5358:1: (lv_value_1_0= True )
                    {
                    // InternalResoluteParser.g:5358:1: (lv_value_1_0= True )
                    // InternalResoluteParser.g:5359:3: lv_value_1_0= True
                    {
                    lv_value_1_0=(Token)match(input,True,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_value_1_0, grammarAccess.getBooleanLiteralAccess().getValueTrueKeyword_1_0_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getBooleanLiteralRule());
                      	        }
                             		setWithLastConsumed(current, "value", true, "true");
                      	    
                    }

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:5375:2: otherlv_2= False
                    {
                    otherlv_2=(Token)match(input,False,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_2, grammarAccess.getBooleanLiteralAccess().getFalseKeyword_1_1());
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBooleanLiteral"


    // $ANTLR start "entryRuleConstantValue"
    // InternalResoluteParser.g:5387:1: entryRuleConstantValue returns [EObject current=null] : iv_ruleConstantValue= ruleConstantValue EOF ;
    public final EObject entryRuleConstantValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConstantValue = null;


        try {
            // InternalResoluteParser.g:5388:2: (iv_ruleConstantValue= ruleConstantValue EOF )
            // InternalResoluteParser.g:5389:2: iv_ruleConstantValue= ruleConstantValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConstantValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleConstantValue=ruleConstantValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConstantValue; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleConstantValue"


    // $ANTLR start "ruleConstantValue"
    // InternalResoluteParser.g:5396:1: ruleConstantValue returns [EObject current=null] : ( ( ruleQPREF ) ) ;
    public final EObject ruleConstantValue() throws RecognitionException {
        EObject current = null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5399:28: ( ( ( ruleQPREF ) ) )
            // InternalResoluteParser.g:5400:1: ( ( ruleQPREF ) )
            {
            // InternalResoluteParser.g:5400:1: ( ( ruleQPREF ) )
            // InternalResoluteParser.g:5401:1: ( ruleQPREF )
            {
            // InternalResoluteParser.g:5401:1: ( ruleQPREF )
            // InternalResoluteParser.g:5402:3: ruleQPREF
            {
            if ( state.backtracking==0 ) {

              			if (current==null) {
              	            current = createModelElement(grammarAccess.getConstantValueRule());
              	        }
                      
            }
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getConstantValueAccess().getNamedValuePropertyConstantCrossReference_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            ruleQPREF();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleConstantValue"


    // $ANTLR start "entryRuleReferenceTerm"
    // InternalResoluteParser.g:5424:1: entryRuleReferenceTerm returns [EObject current=null] : iv_ruleReferenceTerm= ruleReferenceTerm EOF ;
    public final EObject entryRuleReferenceTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleReferenceTerm = null;


        try {
            // InternalResoluteParser.g:5425:2: (iv_ruleReferenceTerm= ruleReferenceTerm EOF )
            // InternalResoluteParser.g:5426:2: iv_ruleReferenceTerm= ruleReferenceTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getReferenceTermRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleReferenceTerm=ruleReferenceTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleReferenceTerm; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleReferenceTerm"


    // $ANTLR start "ruleReferenceTerm"
    // InternalResoluteParser.g:5433:1: ruleReferenceTerm returns [EObject current=null] : (otherlv_0= Reference otherlv_1= LeftParenthesis ( (lv_path_2_0= ruleContainmentPathElement ) ) otherlv_3= RightParenthesis ) ;
    public final EObject ruleReferenceTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_path_2_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5436:28: ( (otherlv_0= Reference otherlv_1= LeftParenthesis ( (lv_path_2_0= ruleContainmentPathElement ) ) otherlv_3= RightParenthesis ) )
            // InternalResoluteParser.g:5437:1: (otherlv_0= Reference otherlv_1= LeftParenthesis ( (lv_path_2_0= ruleContainmentPathElement ) ) otherlv_3= RightParenthesis )
            {
            // InternalResoluteParser.g:5437:1: (otherlv_0= Reference otherlv_1= LeftParenthesis ( (lv_path_2_0= ruleContainmentPathElement ) ) otherlv_3= RightParenthesis )
            // InternalResoluteParser.g:5438:2: otherlv_0= Reference otherlv_1= LeftParenthesis ( (lv_path_2_0= ruleContainmentPathElement ) ) otherlv_3= RightParenthesis
            {
            otherlv_0=(Token)match(input,Reference,FollowSets000.FOLLOW_14); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getReferenceTermAccess().getReferenceKeyword_0());
                  
            }
            otherlv_1=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getReferenceTermAccess().getLeftParenthesisKeyword_1());
                  
            }
            // InternalResoluteParser.g:5447:1: ( (lv_path_2_0= ruleContainmentPathElement ) )
            // InternalResoluteParser.g:5448:1: (lv_path_2_0= ruleContainmentPathElement )
            {
            // InternalResoluteParser.g:5448:1: (lv_path_2_0= ruleContainmentPathElement )
            // InternalResoluteParser.g:5449:3: lv_path_2_0= ruleContainmentPathElement
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getReferenceTermAccess().getPathContainmentPathElementParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_34);
            lv_path_2_0=ruleContainmentPathElement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getReferenceTermRule());
              	        }
                     		set(
                     			current, 
                     			"path",
                      		lv_path_2_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.ContainmentPathElement");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            otherlv_3=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_3, grammarAccess.getReferenceTermAccess().getRightParenthesisKeyword_3());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleReferenceTerm"


    // $ANTLR start "entryRuleRecordTerm"
    // InternalResoluteParser.g:5478:1: entryRuleRecordTerm returns [EObject current=null] : iv_ruleRecordTerm= ruleRecordTerm EOF ;
    public final EObject entryRuleRecordTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRecordTerm = null;


        try {
            // InternalResoluteParser.g:5479:2: (iv_ruleRecordTerm= ruleRecordTerm EOF )
            // InternalResoluteParser.g:5480:2: iv_ruleRecordTerm= ruleRecordTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRecordTermRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleRecordTerm=ruleRecordTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRecordTerm; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRecordTerm"


    // $ANTLR start "ruleRecordTerm"
    // InternalResoluteParser.g:5487:1: ruleRecordTerm returns [EObject current=null] : (otherlv_0= LeftSquareBracket ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+ otherlv_2= RightSquareBracket ) ;
    public final EObject ruleRecordTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        EObject lv_ownedFieldValue_1_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5490:28: ( (otherlv_0= LeftSquareBracket ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+ otherlv_2= RightSquareBracket ) )
            // InternalResoluteParser.g:5491:1: (otherlv_0= LeftSquareBracket ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+ otherlv_2= RightSquareBracket )
            {
            // InternalResoluteParser.g:5491:1: (otherlv_0= LeftSquareBracket ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+ otherlv_2= RightSquareBracket )
            // InternalResoluteParser.g:5492:2: otherlv_0= LeftSquareBracket ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+ otherlv_2= RightSquareBracket
            {
            otherlv_0=(Token)match(input,LeftSquareBracket,FollowSets000.FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getRecordTermAccess().getLeftSquareBracketKeyword_0());
                  
            }
            // InternalResoluteParser.g:5496:1: ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+
            int cnt68=0;
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==RULE_ID) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // InternalResoluteParser.g:5497:1: (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation )
            	    {
            	    // InternalResoluteParser.g:5497:1: (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation )
            	    // InternalResoluteParser.g:5498:3: lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getRecordTermAccess().getOwnedFieldValueFieldPropertyAssociationParserRuleCall_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_58);
            	    lv_ownedFieldValue_1_0=ruleFieldPropertyAssociation();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getRecordTermRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"ownedFieldValue",
            	              		lv_ownedFieldValue_1_0, 
            	              		"org.osate.xtext.aadl2.properties.Properties.FieldPropertyAssociation");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt68 >= 1 ) break loop68;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(68, input);
                        throw eee;
                }
                cnt68++;
            } while (true);

            otherlv_2=(Token)match(input,RightSquareBracket,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getRecordTermAccess().getRightSquareBracketKeyword_2());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRecordTerm"


    // $ANTLR start "entryRuleComputedTerm"
    // InternalResoluteParser.g:5529:1: entryRuleComputedTerm returns [EObject current=null] : iv_ruleComputedTerm= ruleComputedTerm EOF ;
    public final EObject entryRuleComputedTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComputedTerm = null;


        try {
            // InternalResoluteParser.g:5530:2: (iv_ruleComputedTerm= ruleComputedTerm EOF )
            // InternalResoluteParser.g:5531:2: iv_ruleComputedTerm= ruleComputedTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getComputedTermRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleComputedTerm=ruleComputedTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleComputedTerm; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleComputedTerm"


    // $ANTLR start "ruleComputedTerm"
    // InternalResoluteParser.g:5538:1: ruleComputedTerm returns [EObject current=null] : (otherlv_0= Compute otherlv_1= LeftParenthesis ( (lv_function_2_0= RULE_ID ) ) otherlv_3= RightParenthesis ) ;
    public final EObject ruleComputedTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token lv_function_2_0=null;
        Token otherlv_3=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5541:28: ( (otherlv_0= Compute otherlv_1= LeftParenthesis ( (lv_function_2_0= RULE_ID ) ) otherlv_3= RightParenthesis ) )
            // InternalResoluteParser.g:5542:1: (otherlv_0= Compute otherlv_1= LeftParenthesis ( (lv_function_2_0= RULE_ID ) ) otherlv_3= RightParenthesis )
            {
            // InternalResoluteParser.g:5542:1: (otherlv_0= Compute otherlv_1= LeftParenthesis ( (lv_function_2_0= RULE_ID ) ) otherlv_3= RightParenthesis )
            // InternalResoluteParser.g:5543:2: otherlv_0= Compute otherlv_1= LeftParenthesis ( (lv_function_2_0= RULE_ID ) ) otherlv_3= RightParenthesis
            {
            otherlv_0=(Token)match(input,Compute,FollowSets000.FOLLOW_14); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getComputedTermAccess().getComputeKeyword_0());
                  
            }
            otherlv_1=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getComputedTermAccess().getLeftParenthesisKeyword_1());
                  
            }
            // InternalResoluteParser.g:5552:1: ( (lv_function_2_0= RULE_ID ) )
            // InternalResoluteParser.g:5553:1: (lv_function_2_0= RULE_ID )
            {
            // InternalResoluteParser.g:5553:1: (lv_function_2_0= RULE_ID )
            // InternalResoluteParser.g:5554:3: lv_function_2_0= RULE_ID
            {
            lv_function_2_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_function_2_0, grammarAccess.getComputedTermAccess().getFunctionIDTerminalRuleCall_2_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getComputedTermRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"function",
                      		lv_function_2_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.ID");
              	    
            }

            }


            }

            otherlv_3=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_3, grammarAccess.getComputedTermAccess().getRightParenthesisKeyword_3());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleComputedTerm"


    // $ANTLR start "entryRuleComponentClassifierTerm"
    // InternalResoluteParser.g:5583:1: entryRuleComponentClassifierTerm returns [EObject current=null] : iv_ruleComponentClassifierTerm= ruleComponentClassifierTerm EOF ;
    public final EObject entryRuleComponentClassifierTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComponentClassifierTerm = null;


        try {
            // InternalResoluteParser.g:5584:2: (iv_ruleComponentClassifierTerm= ruleComponentClassifierTerm EOF )
            // InternalResoluteParser.g:5585:2: iv_ruleComponentClassifierTerm= ruleComponentClassifierTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getComponentClassifierTermRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleComponentClassifierTerm=ruleComponentClassifierTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleComponentClassifierTerm; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleComponentClassifierTerm"


    // $ANTLR start "ruleComponentClassifierTerm"
    // InternalResoluteParser.g:5592:1: ruleComponentClassifierTerm returns [EObject current=null] : (otherlv_0= Classifier otherlv_1= LeftParenthesis ( ( ruleQCREF ) ) otherlv_3= RightParenthesis ) ;
    public final EObject ruleComponentClassifierTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5595:28: ( (otherlv_0= Classifier otherlv_1= LeftParenthesis ( ( ruleQCREF ) ) otherlv_3= RightParenthesis ) )
            // InternalResoluteParser.g:5596:1: (otherlv_0= Classifier otherlv_1= LeftParenthesis ( ( ruleQCREF ) ) otherlv_3= RightParenthesis )
            {
            // InternalResoluteParser.g:5596:1: (otherlv_0= Classifier otherlv_1= LeftParenthesis ( ( ruleQCREF ) ) otherlv_3= RightParenthesis )
            // InternalResoluteParser.g:5597:2: otherlv_0= Classifier otherlv_1= LeftParenthesis ( ( ruleQCREF ) ) otherlv_3= RightParenthesis
            {
            otherlv_0=(Token)match(input,Classifier,FollowSets000.FOLLOW_14); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getComponentClassifierTermAccess().getClassifierKeyword_0());
                  
            }
            otherlv_1=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getComponentClassifierTermAccess().getLeftParenthesisKeyword_1());
                  
            }
            // InternalResoluteParser.g:5606:1: ( ( ruleQCREF ) )
            // InternalResoluteParser.g:5607:1: ( ruleQCREF )
            {
            // InternalResoluteParser.g:5607:1: ( ruleQCREF )
            // InternalResoluteParser.g:5608:3: ruleQCREF
            {
            if ( state.backtracking==0 ) {

              			if (current==null) {
              	            current = createModelElement(grammarAccess.getComponentClassifierTermRule());
              	        }
                      
            }
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getComponentClassifierTermAccess().getClassifierComponentClassifierCrossReference_2_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_34);
            ruleQCREF();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            otherlv_3=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_3, grammarAccess.getComponentClassifierTermAccess().getRightParenthesisKeyword_3());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleComponentClassifierTerm"


    // $ANTLR start "entryRuleListTerm"
    // InternalResoluteParser.g:5635:1: entryRuleListTerm returns [EObject current=null] : iv_ruleListTerm= ruleListTerm EOF ;
    public final EObject entryRuleListTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleListTerm = null;


        try {
            // InternalResoluteParser.g:5636:2: (iv_ruleListTerm= ruleListTerm EOF )
            // InternalResoluteParser.g:5637:2: iv_ruleListTerm= ruleListTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getListTermRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleListTerm=ruleListTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleListTerm; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleListTerm"


    // $ANTLR start "ruleListTerm"
    // InternalResoluteParser.g:5644:1: ruleListTerm returns [EObject current=null] : ( () otherlv_1= LeftParenthesis ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )? otherlv_5= RightParenthesis ) ;
    public final EObject ruleListTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_ownedListElement_2_0 = null;

        EObject lv_ownedListElement_4_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5647:28: ( ( () otherlv_1= LeftParenthesis ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )? otherlv_5= RightParenthesis ) )
            // InternalResoluteParser.g:5648:1: ( () otherlv_1= LeftParenthesis ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )? otherlv_5= RightParenthesis )
            {
            // InternalResoluteParser.g:5648:1: ( () otherlv_1= LeftParenthesis ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )? otherlv_5= RightParenthesis )
            // InternalResoluteParser.g:5648:2: () otherlv_1= LeftParenthesis ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )? otherlv_5= RightParenthesis
            {
            // InternalResoluteParser.g:5648:2: ()
            // InternalResoluteParser.g:5649:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getListTermAccess().getListValueAction_0(),
                          current);
                  
            }

            }

            otherlv_1=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_59); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getListTermAccess().getLeftParenthesisKeyword_1());
                  
            }
            // InternalResoluteParser.g:5659:1: ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )?
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==Classifier||LA70_0==Reference||LA70_0==Compute||LA70_0==False||LA70_0==True||LA70_0==LeftParenthesis||LA70_0==PlusSign||LA70_0==HyphenMinus||LA70_0==LeftSquareBracket||LA70_0==RULE_REAL_LIT||LA70_0==RULE_INTEGER_LIT||(LA70_0>=RULE_STRING && LA70_0<=RULE_ID)) ) {
                alt70=1;
            }
            switch (alt70) {
                case 1 :
                    // InternalResoluteParser.g:5659:2: ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )*
                    {
                    // InternalResoluteParser.g:5659:2: ( (lv_ownedListElement_2_0= rulePropertyExpression ) )
                    // InternalResoluteParser.g:5660:1: (lv_ownedListElement_2_0= rulePropertyExpression )
                    {
                    // InternalResoluteParser.g:5660:1: (lv_ownedListElement_2_0= rulePropertyExpression )
                    // InternalResoluteParser.g:5661:3: lv_ownedListElement_2_0= rulePropertyExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getListTermAccess().getOwnedListElementPropertyExpressionParserRuleCall_2_0_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_16);
                    lv_ownedListElement_2_0=rulePropertyExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getListTermRule());
                      	        }
                             		add(
                             			current, 
                             			"ownedListElement",
                              		lv_ownedListElement_2_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.PropertyExpression");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // InternalResoluteParser.g:5677:2: (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )*
                    loop69:
                    do {
                        int alt69=2;
                        int LA69_0 = input.LA(1);

                        if ( (LA69_0==Comma) ) {
                            alt69=1;
                        }


                        switch (alt69) {
                    	case 1 :
                    	    // InternalResoluteParser.g:5678:2: otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) )
                    	    {
                    	    otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_54); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	          	newLeafNode(otherlv_3, grammarAccess.getListTermAccess().getCommaKeyword_2_1_0());
                    	          
                    	    }
                    	    // InternalResoluteParser.g:5682:1: ( (lv_ownedListElement_4_0= rulePropertyExpression ) )
                    	    // InternalResoluteParser.g:5683:1: (lv_ownedListElement_4_0= rulePropertyExpression )
                    	    {
                    	    // InternalResoluteParser.g:5683:1: (lv_ownedListElement_4_0= rulePropertyExpression )
                    	    // InternalResoluteParser.g:5684:3: lv_ownedListElement_4_0= rulePropertyExpression
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getListTermAccess().getOwnedListElementPropertyExpressionParserRuleCall_2_1_1_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_16);
                    	    lv_ownedListElement_4_0=rulePropertyExpression();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getListTermRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"ownedListElement",
                    	              		lv_ownedListElement_4_0, 
                    	              		"org.osate.xtext.aadl2.properties.Properties.PropertyExpression");
                    	      	        afterParserOrEnumRuleCall();
                    	      	    
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop69;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_5=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_5, grammarAccess.getListTermAccess().getRightParenthesisKeyword_3());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleListTerm"


    // $ANTLR start "entryRuleFieldPropertyAssociation"
    // InternalResoluteParser.g:5713:1: entryRuleFieldPropertyAssociation returns [EObject current=null] : iv_ruleFieldPropertyAssociation= ruleFieldPropertyAssociation EOF ;
    public final EObject entryRuleFieldPropertyAssociation() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFieldPropertyAssociation = null;


        try {
            // InternalResoluteParser.g:5714:2: (iv_ruleFieldPropertyAssociation= ruleFieldPropertyAssociation EOF )
            // InternalResoluteParser.g:5715:2: iv_ruleFieldPropertyAssociation= ruleFieldPropertyAssociation EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFieldPropertyAssociationRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleFieldPropertyAssociation=ruleFieldPropertyAssociation();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFieldPropertyAssociation; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleFieldPropertyAssociation"


    // $ANTLR start "ruleFieldPropertyAssociation"
    // InternalResoluteParser.g:5722:1: ruleFieldPropertyAssociation returns [EObject current=null] : ( ( (otherlv_0= RULE_ID ) ) otherlv_1= EqualsSignGreaterThanSign ( (lv_ownedValue_2_0= rulePropertyExpression ) ) otherlv_3= Semicolon ) ;
    public final EObject ruleFieldPropertyAssociation() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_ownedValue_2_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5725:28: ( ( ( (otherlv_0= RULE_ID ) ) otherlv_1= EqualsSignGreaterThanSign ( (lv_ownedValue_2_0= rulePropertyExpression ) ) otherlv_3= Semicolon ) )
            // InternalResoluteParser.g:5726:1: ( ( (otherlv_0= RULE_ID ) ) otherlv_1= EqualsSignGreaterThanSign ( (lv_ownedValue_2_0= rulePropertyExpression ) ) otherlv_3= Semicolon )
            {
            // InternalResoluteParser.g:5726:1: ( ( (otherlv_0= RULE_ID ) ) otherlv_1= EqualsSignGreaterThanSign ( (lv_ownedValue_2_0= rulePropertyExpression ) ) otherlv_3= Semicolon )
            // InternalResoluteParser.g:5726:2: ( (otherlv_0= RULE_ID ) ) otherlv_1= EqualsSignGreaterThanSign ( (lv_ownedValue_2_0= rulePropertyExpression ) ) otherlv_3= Semicolon
            {
            // InternalResoluteParser.g:5726:2: ( (otherlv_0= RULE_ID ) )
            // InternalResoluteParser.g:5727:1: (otherlv_0= RULE_ID )
            {
            // InternalResoluteParser.g:5727:1: (otherlv_0= RULE_ID )
            // InternalResoluteParser.g:5728:3: otherlv_0= RULE_ID
            {
            if ( state.backtracking==0 ) {

              			if (current==null) {
              	            current = createModelElement(grammarAccess.getFieldPropertyAssociationRule());
              	        }
                      
            }
            otherlv_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_60); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		newLeafNode(otherlv_0, grammarAccess.getFieldPropertyAssociationAccess().getPropertyBasicPropertyCrossReference_0_0()); 
              	
            }

            }


            }

            otherlv_1=(Token)match(input,EqualsSignGreaterThanSign,FollowSets000.FOLLOW_54); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getFieldPropertyAssociationAccess().getEqualsSignGreaterThanSignKeyword_1());
                  
            }
            // InternalResoluteParser.g:5744:1: ( (lv_ownedValue_2_0= rulePropertyExpression ) )
            // InternalResoluteParser.g:5745:1: (lv_ownedValue_2_0= rulePropertyExpression )
            {
            // InternalResoluteParser.g:5745:1: (lv_ownedValue_2_0= rulePropertyExpression )
            // InternalResoluteParser.g:5746:3: lv_ownedValue_2_0= rulePropertyExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getFieldPropertyAssociationAccess().getOwnedValuePropertyExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_47);
            lv_ownedValue_2_0=rulePropertyExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getFieldPropertyAssociationRule());
              	        }
                     		set(
                     			current, 
                     			"ownedValue",
                      		lv_ownedValue_2_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.PropertyExpression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            otherlv_3=(Token)match(input,Semicolon,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_3, grammarAccess.getFieldPropertyAssociationAccess().getSemicolonKeyword_3());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleFieldPropertyAssociation"


    // $ANTLR start "entryRuleContainmentPathElement"
    // InternalResoluteParser.g:5775:1: entryRuleContainmentPathElement returns [EObject current=null] : iv_ruleContainmentPathElement= ruleContainmentPathElement EOF ;
    public final EObject entryRuleContainmentPathElement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleContainmentPathElement = null;


        try {
            // InternalResoluteParser.g:5776:2: (iv_ruleContainmentPathElement= ruleContainmentPathElement EOF )
            // InternalResoluteParser.g:5777:2: iv_ruleContainmentPathElement= ruleContainmentPathElement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getContainmentPathElementRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleContainmentPathElement=ruleContainmentPathElement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleContainmentPathElement; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleContainmentPathElement"


    // $ANTLR start "ruleContainmentPathElement"
    // InternalResoluteParser.g:5784:1: ruleContainmentPathElement returns [EObject current=null] : ( ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* ) (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )? ) ;
    public final EObject ruleContainmentPathElement() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        EObject lv_arrayRange_1_0 = null;

        EObject lv_path_3_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5787:28: ( ( ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* ) (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )? ) )
            // InternalResoluteParser.g:5788:1: ( ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* ) (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )? )
            {
            // InternalResoluteParser.g:5788:1: ( ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* ) (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )? )
            // InternalResoluteParser.g:5788:2: ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* ) (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )?
            {
            // InternalResoluteParser.g:5788:2: ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* )
            // InternalResoluteParser.g:5788:3: ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )*
            {
            // InternalResoluteParser.g:5788:3: ( (otherlv_0= RULE_ID ) )
            // InternalResoluteParser.g:5789:1: (otherlv_0= RULE_ID )
            {
            // InternalResoluteParser.g:5789:1: (otherlv_0= RULE_ID )
            // InternalResoluteParser.g:5790:3: otherlv_0= RULE_ID
            {
            if ( state.backtracking==0 ) {

              			if (current==null) {
              	            current = createModelElement(grammarAccess.getContainmentPathElementRule());
              	        }
                      
            }
            otherlv_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_61); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		newLeafNode(otherlv_0, grammarAccess.getContainmentPathElementAccess().getNamedElementNamedElementCrossReference_0_0_0()); 
              	
            }

            }


            }

            // InternalResoluteParser.g:5801:2: ( (lv_arrayRange_1_0= ruleArrayRange ) )*
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);

                if ( (LA71_0==LeftSquareBracket) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // InternalResoluteParser.g:5802:1: (lv_arrayRange_1_0= ruleArrayRange )
            	    {
            	    // InternalResoluteParser.g:5802:1: (lv_arrayRange_1_0= ruleArrayRange )
            	    // InternalResoluteParser.g:5803:3: lv_arrayRange_1_0= ruleArrayRange
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getContainmentPathElementAccess().getArrayRangeArrayRangeParserRuleCall_0_1_0()); 
            	      	    
            	    }
            	    pushFollow(FollowSets000.FOLLOW_61);
            	    lv_arrayRange_1_0=ruleArrayRange();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getContainmentPathElementRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"arrayRange",
            	              		lv_arrayRange_1_0, 
            	              		"org.osate.xtext.aadl2.properties.Properties.ArrayRange");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop71;
                }
            } while (true);


            }

            // InternalResoluteParser.g:5819:4: (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )?
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==FullStop) ) {
                alt72=1;
            }
            switch (alt72) {
                case 1 :
                    // InternalResoluteParser.g:5820:2: otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) )
                    {
                    otherlv_2=(Token)match(input,FullStop,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_2, grammarAccess.getContainmentPathElementAccess().getFullStopKeyword_1_0());
                          
                    }
                    // InternalResoluteParser.g:5824:1: ( (lv_path_3_0= ruleContainmentPathElement ) )
                    // InternalResoluteParser.g:5825:1: (lv_path_3_0= ruleContainmentPathElement )
                    {
                    // InternalResoluteParser.g:5825:1: (lv_path_3_0= ruleContainmentPathElement )
                    // InternalResoluteParser.g:5826:3: lv_path_3_0= ruleContainmentPathElement
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getContainmentPathElementAccess().getPathContainmentPathElementParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_path_3_0=ruleContainmentPathElement();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getContainmentPathElementRule());
                      	        }
                             		set(
                             			current, 
                             			"path",
                              		lv_path_3_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.ContainmentPathElement");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleContainmentPathElement"


    // $ANTLR start "entryRulePlusMinus"
    // InternalResoluteParser.g:5852:1: entryRulePlusMinus returns [String current=null] : iv_rulePlusMinus= rulePlusMinus EOF ;
    public final String entryRulePlusMinus() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePlusMinus = null;


        try {
            // InternalResoluteParser.g:5853:1: (iv_rulePlusMinus= rulePlusMinus EOF )
            // InternalResoluteParser.g:5854:2: iv_rulePlusMinus= rulePlusMinus EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPlusMinusRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_rulePlusMinus=rulePlusMinus();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePlusMinus.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePlusMinus"


    // $ANTLR start "rulePlusMinus"
    // InternalResoluteParser.g:5861:1: rulePlusMinus returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= PlusSign | kw= HyphenMinus ) ;
    public final AntlrDatatypeRuleToken rulePlusMinus() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5865:6: ( (kw= PlusSign | kw= HyphenMinus ) )
            // InternalResoluteParser.g:5866:1: (kw= PlusSign | kw= HyphenMinus )
            {
            // InternalResoluteParser.g:5866:1: (kw= PlusSign | kw= HyphenMinus )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==PlusSign) ) {
                alt73=1;
            }
            else if ( (LA73_0==HyphenMinus) ) {
                alt73=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // InternalResoluteParser.g:5867:2: kw= PlusSign
                    {
                    kw=(Token)match(input,PlusSign,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getPlusMinusAccess().getPlusSignKeyword_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:5874:2: kw= HyphenMinus
                    {
                    kw=(Token)match(input,HyphenMinus,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getPlusMinusAccess().getHyphenMinusKeyword_1()); 
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePlusMinus"


    // $ANTLR start "entryRuleStringTerm"
    // InternalResoluteParser.g:5887:1: entryRuleStringTerm returns [EObject current=null] : iv_ruleStringTerm= ruleStringTerm EOF ;
    public final EObject entryRuleStringTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringTerm = null;


        try {
            // InternalResoluteParser.g:5888:2: (iv_ruleStringTerm= ruleStringTerm EOF )
            // InternalResoluteParser.g:5889:2: iv_ruleStringTerm= ruleStringTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getStringTermRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleStringTerm=ruleStringTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleStringTerm; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStringTerm"


    // $ANTLR start "ruleStringTerm"
    // InternalResoluteParser.g:5896:1: ruleStringTerm returns [EObject current=null] : ( (lv_value_0_0= ruleNoQuoteString ) ) ;
    public final EObject ruleStringTerm() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_value_0_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5899:28: ( ( (lv_value_0_0= ruleNoQuoteString ) ) )
            // InternalResoluteParser.g:5900:1: ( (lv_value_0_0= ruleNoQuoteString ) )
            {
            // InternalResoluteParser.g:5900:1: ( (lv_value_0_0= ruleNoQuoteString ) )
            // InternalResoluteParser.g:5901:1: (lv_value_0_0= ruleNoQuoteString )
            {
            // InternalResoluteParser.g:5901:1: (lv_value_0_0= ruleNoQuoteString )
            // InternalResoluteParser.g:5902:3: lv_value_0_0= ruleNoQuoteString
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getStringTermAccess().getValueNoQuoteStringParserRuleCall_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_0_0=ruleNoQuoteString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getStringTermRule());
              	        }
                     		set(
                     			current, 
                     			"value",
                      		lv_value_0_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.NoQuoteString");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStringTerm"


    // $ANTLR start "entryRuleNoQuoteString"
    // InternalResoluteParser.g:5926:1: entryRuleNoQuoteString returns [String current=null] : iv_ruleNoQuoteString= ruleNoQuoteString EOF ;
    public final String entryRuleNoQuoteString() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNoQuoteString = null;


        try {
            // InternalResoluteParser.g:5927:1: (iv_ruleNoQuoteString= ruleNoQuoteString EOF )
            // InternalResoluteParser.g:5928:2: iv_ruleNoQuoteString= ruleNoQuoteString EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNoQuoteStringRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNoQuoteString=ruleNoQuoteString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNoQuoteString.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNoQuoteString"


    // $ANTLR start "ruleNoQuoteString"
    // InternalResoluteParser.g:5935:1: ruleNoQuoteString returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleNoQuoteString() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5939:6: (this_STRING_0= RULE_STRING )
            // InternalResoluteParser.g:5940:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_STRING_0);
                  
            }
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_STRING_0, grammarAccess.getNoQuoteStringAccess().getSTRINGTerminalRuleCall()); 
                  
            }

            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNoQuoteString"


    // $ANTLR start "entryRuleArrayRange"
    // InternalResoluteParser.g:5955:1: entryRuleArrayRange returns [EObject current=null] : iv_ruleArrayRange= ruleArrayRange EOF ;
    public final EObject entryRuleArrayRange() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArrayRange = null;


        try {
            // InternalResoluteParser.g:5956:2: (iv_ruleArrayRange= ruleArrayRange EOF )
            // InternalResoluteParser.g:5957:2: iv_ruleArrayRange= ruleArrayRange EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getArrayRangeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleArrayRange=ruleArrayRange();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleArrayRange; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleArrayRange"


    // $ANTLR start "ruleArrayRange"
    // InternalResoluteParser.g:5964:1: ruleArrayRange returns [EObject current=null] : ( () otherlv_1= LeftSquareBracket ( (lv_lowerBound_2_0= ruleINTVALUE ) ) (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )? otherlv_5= RightSquareBracket ) ;
    public final EObject ruleArrayRange() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        AntlrDatatypeRuleToken lv_lowerBound_2_0 = null;

        AntlrDatatypeRuleToken lv_upperBound_4_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:5967:28: ( ( () otherlv_1= LeftSquareBracket ( (lv_lowerBound_2_0= ruleINTVALUE ) ) (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )? otherlv_5= RightSquareBracket ) )
            // InternalResoluteParser.g:5968:1: ( () otherlv_1= LeftSquareBracket ( (lv_lowerBound_2_0= ruleINTVALUE ) ) (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )? otherlv_5= RightSquareBracket )
            {
            // InternalResoluteParser.g:5968:1: ( () otherlv_1= LeftSquareBracket ( (lv_lowerBound_2_0= ruleINTVALUE ) ) (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )? otherlv_5= RightSquareBracket )
            // InternalResoluteParser.g:5968:2: () otherlv_1= LeftSquareBracket ( (lv_lowerBound_2_0= ruleINTVALUE ) ) (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )? otherlv_5= RightSquareBracket
            {
            // InternalResoluteParser.g:5968:2: ()
            // InternalResoluteParser.g:5969:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getArrayRangeAccess().getArrayRangeAction_0(),
                          current);
                  
            }

            }

            otherlv_1=(Token)match(input,LeftSquareBracket,FollowSets000.FOLLOW_62); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getArrayRangeAccess().getLeftSquareBracketKeyword_1());
                  
            }
            // InternalResoluteParser.g:5979:1: ( (lv_lowerBound_2_0= ruleINTVALUE ) )
            // InternalResoluteParser.g:5980:1: (lv_lowerBound_2_0= ruleINTVALUE )
            {
            // InternalResoluteParser.g:5980:1: (lv_lowerBound_2_0= ruleINTVALUE )
            // InternalResoluteParser.g:5981:3: lv_lowerBound_2_0= ruleINTVALUE
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getArrayRangeAccess().getLowerBoundINTVALUEParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_63);
            lv_lowerBound_2_0=ruleINTVALUE();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getArrayRangeRule());
              	        }
                     		set(
                     			current, 
                     			"lowerBound",
                      		lv_lowerBound_2_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.INTVALUE");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // InternalResoluteParser.g:5997:2: (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==FullStopFullStop) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // InternalResoluteParser.g:5998:2: otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) )
                    {
                    otherlv_3=(Token)match(input,FullStopFullStop,FollowSets000.FOLLOW_62); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_3, grammarAccess.getArrayRangeAccess().getFullStopFullStopKeyword_3_0());
                          
                    }
                    // InternalResoluteParser.g:6002:1: ( (lv_upperBound_4_0= ruleINTVALUE ) )
                    // InternalResoluteParser.g:6003:1: (lv_upperBound_4_0= ruleINTVALUE )
                    {
                    // InternalResoluteParser.g:6003:1: (lv_upperBound_4_0= ruleINTVALUE )
                    // InternalResoluteParser.g:6004:3: lv_upperBound_4_0= ruleINTVALUE
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getArrayRangeAccess().getUpperBoundINTVALUEParserRuleCall_3_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_5);
                    lv_upperBound_4_0=ruleINTVALUE();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getArrayRangeRule());
                      	        }
                             		set(
                             			current, 
                             			"upperBound",
                              		lv_upperBound_4_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.INTVALUE");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }

            otherlv_5=(Token)match(input,RightSquareBracket,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_5, grammarAccess.getArrayRangeAccess().getRightSquareBracketKeyword_4());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleArrayRange"


    // $ANTLR start "entryRuleSignedConstant"
    // InternalResoluteParser.g:6033:1: entryRuleSignedConstant returns [EObject current=null] : iv_ruleSignedConstant= ruleSignedConstant EOF ;
    public final EObject entryRuleSignedConstant() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSignedConstant = null;


        try {
            // InternalResoluteParser.g:6034:2: (iv_ruleSignedConstant= ruleSignedConstant EOF )
            // InternalResoluteParser.g:6035:2: iv_ruleSignedConstant= ruleSignedConstant EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSignedConstantRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSignedConstant=ruleSignedConstant();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSignedConstant; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSignedConstant"


    // $ANTLR start "ruleSignedConstant"
    // InternalResoluteParser.g:6042:1: ruleSignedConstant returns [EObject current=null] : ( ( (lv_op_0_0= rulePlusMinus ) ) ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) ) ) ;
    public final EObject ruleSignedConstant() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_op_0_0 = null;

        EObject lv_ownedPropertyExpression_1_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:6045:28: ( ( ( (lv_op_0_0= rulePlusMinus ) ) ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) ) ) )
            // InternalResoluteParser.g:6046:1: ( ( (lv_op_0_0= rulePlusMinus ) ) ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) ) )
            {
            // InternalResoluteParser.g:6046:1: ( ( (lv_op_0_0= rulePlusMinus ) ) ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) ) )
            // InternalResoluteParser.g:6046:2: ( (lv_op_0_0= rulePlusMinus ) ) ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) )
            {
            // InternalResoluteParser.g:6046:2: ( (lv_op_0_0= rulePlusMinus ) )
            // InternalResoluteParser.g:6047:1: (lv_op_0_0= rulePlusMinus )
            {
            // InternalResoluteParser.g:6047:1: (lv_op_0_0= rulePlusMinus )
            // InternalResoluteParser.g:6048:3: lv_op_0_0= rulePlusMinus
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getSignedConstantAccess().getOpPlusMinusParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_64);
            lv_op_0_0=rulePlusMinus();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getSignedConstantRule());
              	        }
                     		set(
                     			current, 
                     			"op",
                      		lv_op_0_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.PlusMinus");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // InternalResoluteParser.g:6064:2: ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) )
            // InternalResoluteParser.g:6065:1: (lv_ownedPropertyExpression_1_0= ruleConstantValue )
            {
            // InternalResoluteParser.g:6065:1: (lv_ownedPropertyExpression_1_0= ruleConstantValue )
            // InternalResoluteParser.g:6066:3: lv_ownedPropertyExpression_1_0= ruleConstantValue
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getSignedConstantAccess().getOwnedPropertyExpressionConstantValueParserRuleCall_1_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_ownedPropertyExpression_1_0=ruleConstantValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getSignedConstantRule());
              	        }
                     		add(
                     			current, 
                     			"ownedPropertyExpression",
                      		lv_ownedPropertyExpression_1_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.ConstantValue");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSignedConstant"


    // $ANTLR start "entryRuleNumericRangeTerm"
    // InternalResoluteParser.g:6094:1: entryRuleNumericRangeTerm returns [EObject current=null] : iv_ruleNumericRangeTerm= ruleNumericRangeTerm EOF ;
    public final EObject entryRuleNumericRangeTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNumericRangeTerm = null;


        try {
            // InternalResoluteParser.g:6095:2: (iv_ruleNumericRangeTerm= ruleNumericRangeTerm EOF )
            // InternalResoluteParser.g:6096:2: iv_ruleNumericRangeTerm= ruleNumericRangeTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNumericRangeTermRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNumericRangeTerm=ruleNumericRangeTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNumericRangeTerm; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNumericRangeTerm"


    // $ANTLR start "ruleNumericRangeTerm"
    // InternalResoluteParser.g:6103:1: ruleNumericRangeTerm returns [EObject current=null] : ( ( (lv_minimum_0_0= ruleNumAlt ) ) otherlv_1= FullStopFullStop ( (lv_maximum_2_0= ruleNumAlt ) ) (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )? ) ;
    public final EObject ruleNumericRangeTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_minimum_0_0 = null;

        EObject lv_maximum_2_0 = null;

        EObject lv_delta_4_0 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:6106:28: ( ( ( (lv_minimum_0_0= ruleNumAlt ) ) otherlv_1= FullStopFullStop ( (lv_maximum_2_0= ruleNumAlt ) ) (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )? ) )
            // InternalResoluteParser.g:6107:1: ( ( (lv_minimum_0_0= ruleNumAlt ) ) otherlv_1= FullStopFullStop ( (lv_maximum_2_0= ruleNumAlt ) ) (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )? )
            {
            // InternalResoluteParser.g:6107:1: ( ( (lv_minimum_0_0= ruleNumAlt ) ) otherlv_1= FullStopFullStop ( (lv_maximum_2_0= ruleNumAlt ) ) (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )? )
            // InternalResoluteParser.g:6107:2: ( (lv_minimum_0_0= ruleNumAlt ) ) otherlv_1= FullStopFullStop ( (lv_maximum_2_0= ruleNumAlt ) ) (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )?
            {
            // InternalResoluteParser.g:6107:2: ( (lv_minimum_0_0= ruleNumAlt ) )
            // InternalResoluteParser.g:6108:1: (lv_minimum_0_0= ruleNumAlt )
            {
            // InternalResoluteParser.g:6108:1: (lv_minimum_0_0= ruleNumAlt )
            // InternalResoluteParser.g:6109:3: lv_minimum_0_0= ruleNumAlt
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getNumericRangeTermAccess().getMinimumNumAltParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_65);
            lv_minimum_0_0=ruleNumAlt();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getNumericRangeTermRule());
              	        }
                     		set(
                     			current, 
                     			"minimum",
                      		lv_minimum_0_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.NumAlt");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            otherlv_1=(Token)match(input,FullStopFullStop,FollowSets000.FOLLOW_64); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getNumericRangeTermAccess().getFullStopFullStopKeyword_1());
                  
            }
            // InternalResoluteParser.g:6130:1: ( (lv_maximum_2_0= ruleNumAlt ) )
            // InternalResoluteParser.g:6131:1: (lv_maximum_2_0= ruleNumAlt )
            {
            // InternalResoluteParser.g:6131:1: (lv_maximum_2_0= ruleNumAlt )
            // InternalResoluteParser.g:6132:3: lv_maximum_2_0= ruleNumAlt
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getNumericRangeTermAccess().getMaximumNumAltParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FollowSets000.FOLLOW_66);
            lv_maximum_2_0=ruleNumAlt();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getNumericRangeTermRule());
              	        }
                     		set(
                     			current, 
                     			"maximum",
                      		lv_maximum_2_0, 
                      		"org.osate.xtext.aadl2.properties.Properties.NumAlt");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // InternalResoluteParser.g:6148:2: (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )?
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==Delta) ) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // InternalResoluteParser.g:6149:2: otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) )
                    {
                    otherlv_3=(Token)match(input,Delta,FollowSets000.FOLLOW_64); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_3, grammarAccess.getNumericRangeTermAccess().getDeltaKeyword_3_0());
                          
                    }
                    // InternalResoluteParser.g:6153:1: ( (lv_delta_4_0= ruleNumAlt ) )
                    // InternalResoluteParser.g:6154:1: (lv_delta_4_0= ruleNumAlt )
                    {
                    // InternalResoluteParser.g:6154:1: (lv_delta_4_0= ruleNumAlt )
                    // InternalResoluteParser.g:6155:3: lv_delta_4_0= ruleNumAlt
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getNumericRangeTermAccess().getDeltaNumAltParserRuleCall_3_1_0()); 
                      	    
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_delta_4_0=ruleNumAlt();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getNumericRangeTermRule());
                      	        }
                             		set(
                             			current, 
                             			"delta",
                              		lv_delta_4_0, 
                              		"org.osate.xtext.aadl2.properties.Properties.NumAlt");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNumericRangeTerm"


    // $ANTLR start "entryRuleNumAlt"
    // InternalResoluteParser.g:6179:1: entryRuleNumAlt returns [EObject current=null] : iv_ruleNumAlt= ruleNumAlt EOF ;
    public final EObject entryRuleNumAlt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNumAlt = null;


        try {
            // InternalResoluteParser.g:6180:2: (iv_ruleNumAlt= ruleNumAlt EOF )
            // InternalResoluteParser.g:6181:2: iv_ruleNumAlt= ruleNumAlt EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNumAltRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNumAlt=ruleNumAlt();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNumAlt; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNumAlt"


    // $ANTLR start "ruleNumAlt"
    // InternalResoluteParser.g:6188:1: ruleNumAlt returns [EObject current=null] : (this_RealTerm_0= ruleRealTerm | this_IntegerTerm_1= ruleIntegerTerm | this_SignedConstant_2= ruleSignedConstant | this_ConstantValue_3= ruleConstantValue ) ;
    public final EObject ruleNumAlt() throws RecognitionException {
        EObject current = null;

        EObject this_RealTerm_0 = null;

        EObject this_IntegerTerm_1 = null;

        EObject this_SignedConstant_2 = null;

        EObject this_ConstantValue_3 = null;


         enterRule(); 
            
        try {
            // InternalResoluteParser.g:6191:28: ( (this_RealTerm_0= ruleRealTerm | this_IntegerTerm_1= ruleIntegerTerm | this_SignedConstant_2= ruleSignedConstant | this_ConstantValue_3= ruleConstantValue ) )
            // InternalResoluteParser.g:6192:1: (this_RealTerm_0= ruleRealTerm | this_IntegerTerm_1= ruleIntegerTerm | this_SignedConstant_2= ruleSignedConstant | this_ConstantValue_3= ruleConstantValue )
            {
            // InternalResoluteParser.g:6192:1: (this_RealTerm_0= ruleRealTerm | this_IntegerTerm_1= ruleIntegerTerm | this_SignedConstant_2= ruleSignedConstant | this_ConstantValue_3= ruleConstantValue )
            int alt76=4;
            switch ( input.LA(1) ) {
            case RULE_REAL_LIT:
                {
                alt76=1;
                }
                break;
            case RULE_INTEGER_LIT:
                {
                alt76=2;
                }
                break;
            case PlusSign:
            case HyphenMinus:
                {
                alt76=3;
                }
                break;
            case RULE_ID:
                {
                alt76=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }

            switch (alt76) {
                case 1 :
                    // InternalResoluteParser.g:6193:5: this_RealTerm_0= ruleRealTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getNumAltAccess().getRealTermParserRuleCall_0()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_RealTerm_0=ruleRealTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_RealTerm_0;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // InternalResoluteParser.g:6203:5: this_IntegerTerm_1= ruleIntegerTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getNumAltAccess().getIntegerTermParserRuleCall_1()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerTerm_1=ruleIntegerTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_IntegerTerm_1;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // InternalResoluteParser.g:6213:5: this_SignedConstant_2= ruleSignedConstant
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getNumAltAccess().getSignedConstantParserRuleCall_2()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_SignedConstant_2=ruleSignedConstant();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_SignedConstant_2;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 4 :
                    // InternalResoluteParser.g:6223:5: this_ConstantValue_3= ruleConstantValue
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getNumAltAccess().getConstantValueParserRuleCall_3()); 
                          
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ConstantValue_3=ruleConstantValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = this_ConstantValue_3;
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNumAlt"


    // $ANTLR start "entryRuleAppliesToKeywords"
    // InternalResoluteParser.g:6239:1: entryRuleAppliesToKeywords returns [String current=null] : iv_ruleAppliesToKeywords= ruleAppliesToKeywords EOF ;
    public final String entryRuleAppliesToKeywords() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAppliesToKeywords = null;


        try {
            // InternalResoluteParser.g:6240:1: (iv_ruleAppliesToKeywords= ruleAppliesToKeywords EOF )
            // InternalResoluteParser.g:6241:2: iv_ruleAppliesToKeywords= ruleAppliesToKeywords EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAppliesToKeywordsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAppliesToKeywords=ruleAppliesToKeywords();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAppliesToKeywords.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAppliesToKeywords"


    // $ANTLR start "ruleAppliesToKeywords"
    // InternalResoluteParser.g:6248:1: ruleAppliesToKeywords returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= Applies kw= To ) ;
    public final AntlrDatatypeRuleToken ruleAppliesToKeywords() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:6252:6: ( (kw= Applies kw= To ) )
            // InternalResoluteParser.g:6253:1: (kw= Applies kw= To )
            {
            // InternalResoluteParser.g:6253:1: (kw= Applies kw= To )
            // InternalResoluteParser.g:6254:2: kw= Applies kw= To
            {
            kw=(Token)match(input,Applies,FollowSets000.FOLLOW_67); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current.merge(kw);
                      newLeafNode(kw, grammarAccess.getAppliesToKeywordsAccess().getAppliesKeyword_0()); 
                  
            }
            kw=(Token)match(input,To,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current.merge(kw);
                      newLeafNode(kw, grammarAccess.getAppliesToKeywordsAccess().getToKeyword_1()); 
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAppliesToKeywords"


    // $ANTLR start "entryRuleInBindingKeywords"
    // InternalResoluteParser.g:6273:1: entryRuleInBindingKeywords returns [String current=null] : iv_ruleInBindingKeywords= ruleInBindingKeywords EOF ;
    public final String entryRuleInBindingKeywords() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleInBindingKeywords = null;


        try {
            // InternalResoluteParser.g:6274:1: (iv_ruleInBindingKeywords= ruleInBindingKeywords EOF )
            // InternalResoluteParser.g:6275:2: iv_ruleInBindingKeywords= ruleInBindingKeywords EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getInBindingKeywordsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleInBindingKeywords=ruleInBindingKeywords();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleInBindingKeywords.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleInBindingKeywords"


    // $ANTLR start "ruleInBindingKeywords"
    // InternalResoluteParser.g:6282:1: ruleInBindingKeywords returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= In kw= Binding ) ;
    public final AntlrDatatypeRuleToken ruleInBindingKeywords() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:6286:6: ( (kw= In kw= Binding ) )
            // InternalResoluteParser.g:6287:1: (kw= In kw= Binding )
            {
            // InternalResoluteParser.g:6287:1: (kw= In kw= Binding )
            // InternalResoluteParser.g:6288:2: kw= In kw= Binding
            {
            kw=(Token)match(input,In,FollowSets000.FOLLOW_68); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current.merge(kw);
                      newLeafNode(kw, grammarAccess.getInBindingKeywordsAccess().getInKeyword_0()); 
                  
            }
            kw=(Token)match(input,Binding,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current.merge(kw);
                      newLeafNode(kw, grammarAccess.getInBindingKeywordsAccess().getBindingKeyword_1()); 
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleInBindingKeywords"


    // $ANTLR start "entryRuleInModesKeywords"
    // InternalResoluteParser.g:6307:1: entryRuleInModesKeywords returns [String current=null] : iv_ruleInModesKeywords= ruleInModesKeywords EOF ;
    public final String entryRuleInModesKeywords() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleInModesKeywords = null;


        try {
            // InternalResoluteParser.g:6308:1: (iv_ruleInModesKeywords= ruleInModesKeywords EOF )
            // InternalResoluteParser.g:6309:2: iv_ruleInModesKeywords= ruleInModesKeywords EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getInModesKeywordsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleInModesKeywords=ruleInModesKeywords();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleInModesKeywords.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleInModesKeywords"


    // $ANTLR start "ruleInModesKeywords"
    // InternalResoluteParser.g:6316:1: ruleInModesKeywords returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= In kw= Modes ) ;
    public final AntlrDatatypeRuleToken ruleInModesKeywords() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:6320:6: ( (kw= In kw= Modes ) )
            // InternalResoluteParser.g:6321:1: (kw= In kw= Modes )
            {
            // InternalResoluteParser.g:6321:1: (kw= In kw= Modes )
            // InternalResoluteParser.g:6322:2: kw= In kw= Modes
            {
            kw=(Token)match(input,In,FollowSets000.FOLLOW_69); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current.merge(kw);
                      newLeafNode(kw, grammarAccess.getInModesKeywordsAccess().getInKeyword_0()); 
                  
            }
            kw=(Token)match(input,Modes,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current.merge(kw);
                      newLeafNode(kw, grammarAccess.getInModesKeywordsAccess().getModesKeyword_1()); 
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleInModesKeywords"


    // $ANTLR start "entryRuleINTVALUE"
    // InternalResoluteParser.g:6341:1: entryRuleINTVALUE returns [String current=null] : iv_ruleINTVALUE= ruleINTVALUE EOF ;
    public final String entryRuleINTVALUE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleINTVALUE = null;


        try {
            // InternalResoluteParser.g:6342:1: (iv_ruleINTVALUE= ruleINTVALUE EOF )
            // InternalResoluteParser.g:6343:2: iv_ruleINTVALUE= ruleINTVALUE EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getINTVALUERule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleINTVALUE=ruleINTVALUE();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleINTVALUE.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleINTVALUE"


    // $ANTLR start "ruleINTVALUE"
    // InternalResoluteParser.g:6350:1: ruleINTVALUE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INTEGER_LIT_0= RULE_INTEGER_LIT ;
    public final AntlrDatatypeRuleToken ruleINTVALUE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INTEGER_LIT_0=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:6354:6: (this_INTEGER_LIT_0= RULE_INTEGER_LIT )
            // InternalResoluteParser.g:6355:5: this_INTEGER_LIT_0= RULE_INTEGER_LIT
            {
            this_INTEGER_LIT_0=(Token)match(input,RULE_INTEGER_LIT,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_INTEGER_LIT_0);
                  
            }
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_INTEGER_LIT_0, grammarAccess.getINTVALUEAccess().getINTEGER_LITTerminalRuleCall()); 
                  
            }

            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleINTVALUE"


    // $ANTLR start "entryRuleQPREF"
    // InternalResoluteParser.g:6372:1: entryRuleQPREF returns [String current=null] : iv_ruleQPREF= ruleQPREF EOF ;
    public final String entryRuleQPREF() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQPREF = null;


        try {
            // InternalResoluteParser.g:6373:1: (iv_ruleQPREF= ruleQPREF EOF )
            // InternalResoluteParser.g:6374:2: iv_ruleQPREF= ruleQPREF EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getQPREFRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleQPREF=ruleQPREF();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleQPREF.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleQPREF"


    // $ANTLR start "ruleQPREF"
    // InternalResoluteParser.g:6381:1: ruleQPREF returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= ColonColon this_ID_2= RULE_ID )? ) ;
    public final AntlrDatatypeRuleToken ruleQPREF() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:6385:6: ( (this_ID_0= RULE_ID (kw= ColonColon this_ID_2= RULE_ID )? ) )
            // InternalResoluteParser.g:6386:1: (this_ID_0= RULE_ID (kw= ColonColon this_ID_2= RULE_ID )? )
            {
            // InternalResoluteParser.g:6386:1: (this_ID_0= RULE_ID (kw= ColonColon this_ID_2= RULE_ID )? )
            // InternalResoluteParser.g:6386:6: this_ID_0= RULE_ID (kw= ColonColon this_ID_2= RULE_ID )?
            {
            this_ID_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_70); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_ID_0);
                  
            }
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_ID_0, grammarAccess.getQPREFAccess().getIDTerminalRuleCall_0()); 
                  
            }
            // InternalResoluteParser.g:6393:1: (kw= ColonColon this_ID_2= RULE_ID )?
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==ColonColon) ) {
                alt77=1;
            }
            switch (alt77) {
                case 1 :
                    // InternalResoluteParser.g:6394:2: kw= ColonColon this_ID_2= RULE_ID
                    {
                    kw=(Token)match(input,ColonColon,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getQPREFAccess().getColonColonKeyword_1_0()); 
                          
                    }
                    this_ID_2=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ID_2);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ID_2, grammarAccess.getQPREFAccess().getIDTerminalRuleCall_1_1()); 
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleQPREF"


    // $ANTLR start "entryRuleQCREF"
    // InternalResoluteParser.g:6414:1: entryRuleQCREF returns [String current=null] : iv_ruleQCREF= ruleQCREF EOF ;
    public final String entryRuleQCREF() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQCREF = null;


        try {
            // InternalResoluteParser.g:6415:1: (iv_ruleQCREF= ruleQCREF EOF )
            // InternalResoluteParser.g:6416:2: iv_ruleQCREF= ruleQCREF EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getQCREFRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleQCREF=ruleQCREF();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleQCREF.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleQCREF"


    // $ANTLR start "ruleQCREF"
    // InternalResoluteParser.g:6423:1: ruleQCREF returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_ID_0= RULE_ID kw= ColonColon )* this_ID_2= RULE_ID (kw= FullStop this_ID_4= RULE_ID )? ) ;
    public final AntlrDatatypeRuleToken ruleQCREF() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;
        Token this_ID_4=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:6427:6: ( ( (this_ID_0= RULE_ID kw= ColonColon )* this_ID_2= RULE_ID (kw= FullStop this_ID_4= RULE_ID )? ) )
            // InternalResoluteParser.g:6428:1: ( (this_ID_0= RULE_ID kw= ColonColon )* this_ID_2= RULE_ID (kw= FullStop this_ID_4= RULE_ID )? )
            {
            // InternalResoluteParser.g:6428:1: ( (this_ID_0= RULE_ID kw= ColonColon )* this_ID_2= RULE_ID (kw= FullStop this_ID_4= RULE_ID )? )
            // InternalResoluteParser.g:6428:2: (this_ID_0= RULE_ID kw= ColonColon )* this_ID_2= RULE_ID (kw= FullStop this_ID_4= RULE_ID )?
            {
            // InternalResoluteParser.g:6428:2: (this_ID_0= RULE_ID kw= ColonColon )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==RULE_ID) ) {
                    int LA78_1 = input.LA(2);

                    if ( (LA78_1==ColonColon) ) {
                        alt78=1;
                    }


                }


                switch (alt78) {
            	case 1 :
            	    // InternalResoluteParser.g:6428:7: this_ID_0= RULE_ID kw= ColonColon
            	    {
            	    this_ID_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_71); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      		current.merge(this_ID_0);
            	          
            	    }
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_ID_0, grammarAccess.getQCREFAccess().getIDTerminalRuleCall_0_0()); 
            	          
            	    }
            	    kw=(Token)match(input,ColonColon,FollowSets000.FOLLOW_10); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	              current.merge(kw);
            	              newLeafNode(kw, grammarAccess.getQCREFAccess().getColonColonKeyword_0_1()); 
            	          
            	    }

            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);

            this_ID_2=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_35); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_ID_2);
                  
            }
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_ID_2, grammarAccess.getQCREFAccess().getIDTerminalRuleCall_1()); 
                  
            }
            // InternalResoluteParser.g:6448:1: (kw= FullStop this_ID_4= RULE_ID )?
            int alt79=2;
            int LA79_0 = input.LA(1);

            if ( (LA79_0==FullStop) ) {
                alt79=1;
            }
            switch (alt79) {
                case 1 :
                    // InternalResoluteParser.g:6449:2: kw= FullStop this_ID_4= RULE_ID
                    {
                    kw=(Token)match(input,FullStop,FollowSets000.FOLLOW_10); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getQCREFAccess().getFullStopKeyword_2_0()); 
                          
                    }
                    this_ID_4=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ID_4);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ID_4, grammarAccess.getQCREFAccess().getIDTerminalRuleCall_2_1()); 
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleQCREF"


    // $ANTLR start "entryRuleSTAR"
    // InternalResoluteParser.g:6469:1: entryRuleSTAR returns [String current=null] : iv_ruleSTAR= ruleSTAR EOF ;
    public final String entryRuleSTAR() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSTAR = null;


        try {
            // InternalResoluteParser.g:6470:1: (iv_ruleSTAR= ruleSTAR EOF )
            // InternalResoluteParser.g:6471:2: iv_ruleSTAR= ruleSTAR EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSTARRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSTAR=ruleSTAR();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSTAR.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSTAR"


    // $ANTLR start "ruleSTAR"
    // InternalResoluteParser.g:6478:1: ruleSTAR returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : kw= Asterisk ;
    public final AntlrDatatypeRuleToken ruleSTAR() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalResoluteParser.g:6482:6: (kw= Asterisk )
            // InternalResoluteParser.g:6484:2: kw= Asterisk
            {
            kw=(Token)match(input,Asterisk,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current.merge(kw);
                      newLeafNode(kw, grammarAccess.getSTARAccess().getAsteriskKeyword()); 
                  
            }

            }

            if ( state.backtracking==0 ) {
               leaveRule();
                  
            }
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSTAR"

    // $ANTLR start synpred1_InternalResoluteParser
    public final void synpred1_InternalResoluteParser_fragment() throws RecognitionException {   
        // InternalResoluteParser.g:982:3: ( ( ( ( RULE_ID ) ) Colon ( ( ruleType ) ) ) )
        // InternalResoluteParser.g:982:4: ( ( ( RULE_ID ) ) Colon ( ( ruleType ) ) )
        {
        // InternalResoluteParser.g:982:4: ( ( ( RULE_ID ) ) Colon ( ( ruleType ) ) )
        // InternalResoluteParser.g:982:5: ( ( RULE_ID ) ) Colon ( ( ruleType ) )
        {
        // InternalResoluteParser.g:982:5: ( ( RULE_ID ) )
        // InternalResoluteParser.g:983:1: ( RULE_ID )
        {
        // InternalResoluteParser.g:983:1: ( RULE_ID )
        // InternalResoluteParser.g:984:1: RULE_ID
        {
        match(input,RULE_ID,FollowSets000.FOLLOW_11); if (state.failed) return ;

        }


        }

        match(input,Colon,FollowSets000.FOLLOW_4); if (state.failed) return ;
        // InternalResoluteParser.g:989:1: ( ( ruleType ) )
        // InternalResoluteParser.g:990:1: ( ruleType )
        {
        // InternalResoluteParser.g:990:1: ( ruleType )
        // InternalResoluteParser.g:991:1: ruleType
        {
        pushFollow(FollowSets000.FOLLOW_2);
        ruleType();

        state._fsp--;
        if (state.failed) return ;

        }


        }


        }


        }
    }
    // $ANTLR end synpred1_InternalResoluteParser

    // $ANTLR start synpred2_InternalResoluteParser
    public final void synpred2_InternalResoluteParser_fragment() throws RecognitionException {   
        // InternalResoluteParser.g:1721:3: ( ( () ( ( EqualsSignGreaterThanSign ) ) ) )
        // InternalResoluteParser.g:1721:4: ( () ( ( EqualsSignGreaterThanSign ) ) )
        {
        // InternalResoluteParser.g:1721:4: ( () ( ( EqualsSignGreaterThanSign ) ) )
        // InternalResoluteParser.g:1721:5: () ( ( EqualsSignGreaterThanSign ) )
        {
        // InternalResoluteParser.g:1721:5: ()
        // InternalResoluteParser.g:1722:1: 
        {
        }

        // InternalResoluteParser.g:1722:2: ( ( EqualsSignGreaterThanSign ) )
        // InternalResoluteParser.g:1723:1: ( EqualsSignGreaterThanSign )
        {
        // InternalResoluteParser.g:1723:1: ( EqualsSignGreaterThanSign )
        // InternalResoluteParser.g:1725:1: EqualsSignGreaterThanSign
        {
        match(input,EqualsSignGreaterThanSign,FollowSets000.FOLLOW_2); if (state.failed) return ;

        }


        }


        }


        }
    }
    // $ANTLR end synpred2_InternalResoluteParser

    // $ANTLR start synpred3_InternalResoluteParser
    public final void synpred3_InternalResoluteParser_fragment() throws RecognitionException {   
        // InternalResoluteParser.g:1799:3: ( ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) ) )
        // InternalResoluteParser.g:1799:4: ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) )
        {
        // InternalResoluteParser.g:1799:4: ( () ( ( ( Or ) ) | ( ( Orelse ) ) ) )
        // InternalResoluteParser.g:1799:5: () ( ( ( Or ) ) | ( ( Orelse ) ) )
        {
        // InternalResoluteParser.g:1799:5: ()
        // InternalResoluteParser.g:1800:1: 
        {
        }

        // InternalResoluteParser.g:1800:2: ( ( ( Or ) ) | ( ( Orelse ) ) )
        int alt80=2;
        int LA80_0 = input.LA(1);

        if ( (LA80_0==Or) ) {
            alt80=1;
        }
        else if ( (LA80_0==Orelse) ) {
            alt80=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 80, 0, input);

            throw nvae;
        }
        switch (alt80) {
            case 1 :
                // InternalResoluteParser.g:1800:3: ( ( Or ) )
                {
                // InternalResoluteParser.g:1800:3: ( ( Or ) )
                // InternalResoluteParser.g:1801:1: ( Or )
                {
                // InternalResoluteParser.g:1801:1: ( Or )
                // InternalResoluteParser.g:1803:1: Or
                {
                match(input,Or,FollowSets000.FOLLOW_2); if (state.failed) return ;

                }


                }


                }
                break;
            case 2 :
                // InternalResoluteParser.g:1808:6: ( ( Orelse ) )
                {
                // InternalResoluteParser.g:1808:6: ( ( Orelse ) )
                // InternalResoluteParser.g:1809:1: ( Orelse )
                {
                // InternalResoluteParser.g:1809:1: ( Orelse )
                // InternalResoluteParser.g:1811:1: Orelse
                {
                match(input,Orelse,FollowSets000.FOLLOW_2); if (state.failed) return ;

                }


                }


                }
                break;

        }


        }


        }
    }
    // $ANTLR end synpred3_InternalResoluteParser

    // $ANTLR start synpred4_InternalResoluteParser
    public final void synpred4_InternalResoluteParser_fragment() throws RecognitionException {   
        // InternalResoluteParser.g:1902:3: ( ( () ( ( ( And ) ) | ( ( Andthen ) ) ) ) )
        // InternalResoluteParser.g:1902:4: ( () ( ( ( And ) ) | ( ( Andthen ) ) ) )
        {
        // InternalResoluteParser.g:1902:4: ( () ( ( ( And ) ) | ( ( Andthen ) ) ) )
        // InternalResoluteParser.g:1902:5: () ( ( ( And ) ) | ( ( Andthen ) ) )
        {
        // InternalResoluteParser.g:1902:5: ()
        // InternalResoluteParser.g:1903:1: 
        {
        }

        // InternalResoluteParser.g:1903:2: ( ( ( And ) ) | ( ( Andthen ) ) )
        int alt81=2;
        int LA81_0 = input.LA(1);

        if ( (LA81_0==And) ) {
            alt81=1;
        }
        else if ( (LA81_0==Andthen) ) {
            alt81=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 81, 0, input);

            throw nvae;
        }
        switch (alt81) {
            case 1 :
                // InternalResoluteParser.g:1903:3: ( ( And ) )
                {
                // InternalResoluteParser.g:1903:3: ( ( And ) )
                // InternalResoluteParser.g:1904:1: ( And )
                {
                // InternalResoluteParser.g:1904:1: ( And )
                // InternalResoluteParser.g:1906:1: And
                {
                match(input,And,FollowSets000.FOLLOW_2); if (state.failed) return ;

                }


                }


                }
                break;
            case 2 :
                // InternalResoluteParser.g:1911:6: ( ( Andthen ) )
                {
                // InternalResoluteParser.g:1911:6: ( ( Andthen ) )
                // InternalResoluteParser.g:1912:1: ( Andthen )
                {
                // InternalResoluteParser.g:1912:1: ( Andthen )
                // InternalResoluteParser.g:1914:1: Andthen
                {
                match(input,Andthen,FollowSets000.FOLLOW_2); if (state.failed) return ;

                }


                }


                }
                break;

        }


        }


        }
    }
    // $ANTLR end synpred4_InternalResoluteParser

    // $ANTLR start synpred5_InternalResoluteParser
    public final void synpred5_InternalResoluteParser_fragment() throws RecognitionException {   
        // InternalResoluteParser.g:2005:3: ( ( () Instanceof ) )
        // InternalResoluteParser.g:2005:4: ( () Instanceof )
        {
        // InternalResoluteParser.g:2005:4: ( () Instanceof )
        // InternalResoluteParser.g:2005:5: () Instanceof
        {
        // InternalResoluteParser.g:2005:5: ()
        // InternalResoluteParser.g:2006:1: 
        {
        }

        match(input,Instanceof,FollowSets000.FOLLOW_2); if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred5_InternalResoluteParser

    // $ANTLR start synpred6_InternalResoluteParser
    public final void synpred6_InternalResoluteParser_fragment() throws RecognitionException {   
        // InternalResoluteParser.g:2130:3: ( ( () ( ( ruleRelationalOp ) ) ) )
        // InternalResoluteParser.g:2130:4: ( () ( ( ruleRelationalOp ) ) )
        {
        // InternalResoluteParser.g:2130:4: ( () ( ( ruleRelationalOp ) ) )
        // InternalResoluteParser.g:2130:5: () ( ( ruleRelationalOp ) )
        {
        // InternalResoluteParser.g:2130:5: ()
        // InternalResoluteParser.g:2131:1: 
        {
        }

        // InternalResoluteParser.g:2131:2: ( ( ruleRelationalOp ) )
        // InternalResoluteParser.g:2132:1: ( ruleRelationalOp )
        {
        // InternalResoluteParser.g:2132:1: ( ruleRelationalOp )
        // InternalResoluteParser.g:2133:1: ruleRelationalOp
        {
        pushFollow(FollowSets000.FOLLOW_2);
        ruleRelationalOp();

        state._fsp--;
        if (state.failed) return ;

        }


        }


        }


        }
    }
    // $ANTLR end synpred6_InternalResoluteParser

    // $ANTLR start synpred7_InternalResoluteParser
    public final void synpred7_InternalResoluteParser_fragment() throws RecognitionException {   
        // InternalResoluteParser.g:2207:3: ( ( () ( ( ( PlusSign | HyphenMinus ) ) ) ) )
        // InternalResoluteParser.g:2207:4: ( () ( ( ( PlusSign | HyphenMinus ) ) ) )
        {
        // InternalResoluteParser.g:2207:4: ( () ( ( ( PlusSign | HyphenMinus ) ) ) )
        // InternalResoluteParser.g:2207:5: () ( ( ( PlusSign | HyphenMinus ) ) )
        {
        // InternalResoluteParser.g:2207:5: ()
        // InternalResoluteParser.g:2208:1: 
        {
        }

        // InternalResoluteParser.g:2208:2: ( ( ( PlusSign | HyphenMinus ) ) )
        // InternalResoluteParser.g:2209:1: ( ( PlusSign | HyphenMinus ) )
        {
        // InternalResoluteParser.g:2209:1: ( ( PlusSign | HyphenMinus ) )
        // InternalResoluteParser.g:2210:1: ( PlusSign | HyphenMinus )
        {
        if ( input.LA(1)==PlusSign||input.LA(1)==HyphenMinus ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        }


        }


        }


        }
    }
    // $ANTLR end synpred7_InternalResoluteParser

    // $ANTLR start synpred8_InternalResoluteParser
    public final void synpred8_InternalResoluteParser_fragment() throws RecognitionException {   
        // InternalResoluteParser.g:2308:3: ( ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) ) )
        // InternalResoluteParser.g:2308:4: ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) )
        {
        // InternalResoluteParser.g:2308:4: ( () ( ( ( Asterisk | Solidus | PercentSign ) ) ) )
        // InternalResoluteParser.g:2308:5: () ( ( ( Asterisk | Solidus | PercentSign ) ) )
        {
        // InternalResoluteParser.g:2308:5: ()
        // InternalResoluteParser.g:2309:1: 
        {
        }

        // InternalResoluteParser.g:2309:2: ( ( ( Asterisk | Solidus | PercentSign ) ) )
        // InternalResoluteParser.g:2310:1: ( ( Asterisk | Solidus | PercentSign ) )
        {
        // InternalResoluteParser.g:2310:1: ( ( Asterisk | Solidus | PercentSign ) )
        // InternalResoluteParser.g:2311:1: ( Asterisk | Solidus | PercentSign )
        {
        if ( input.LA(1)==PercentSign||input.LA(1)==Asterisk||input.LA(1)==Solidus ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        }


        }


        }


        }
    }
    // $ANTLR end synpred8_InternalResoluteParser

    // $ANTLR start synpred9_InternalResoluteParser
    public final void synpred9_InternalResoluteParser_fragment() throws RecognitionException {   
        // InternalResoluteParser.g:2426:3: ( ( () ( ( CircumflexAccent ) ) ) )
        // InternalResoluteParser.g:2426:4: ( () ( ( CircumflexAccent ) ) )
        {
        // InternalResoluteParser.g:2426:4: ( () ( ( CircumflexAccent ) ) )
        // InternalResoluteParser.g:2426:5: () ( ( CircumflexAccent ) )
        {
        // InternalResoluteParser.g:2426:5: ()
        // InternalResoluteParser.g:2427:1: 
        {
        }

        // InternalResoluteParser.g:2427:2: ( ( CircumflexAccent ) )
        // InternalResoluteParser.g:2428:1: ( CircumflexAccent )
        {
        // InternalResoluteParser.g:2428:1: ( CircumflexAccent )
        // InternalResoluteParser.g:2430:1: CircumflexAccent
        {
        match(input,CircumflexAccent,FollowSets000.FOLLOW_2); if (state.failed) return ;

        }


        }


        }


        }
    }
    // $ANTLR end synpred9_InternalResoluteParser

    // $ANTLR start synpred10_InternalResoluteParser
    public final void synpred10_InternalResoluteParser_fragment() throws RecognitionException {   
        // InternalResoluteParser.g:3018:7: ( ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis ) )
        // InternalResoluteParser.g:3018:8: ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis )
        {
        // InternalResoluteParser.g:3018:8: ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis )
        // InternalResoluteParser.g:3018:9: () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis
        {
        // InternalResoluteParser.g:3018:9: ()
        // InternalResoluteParser.g:3019:1: 
        {
        }

        // InternalResoluteParser.g:3019:2: ( ( RULE_ID ) )
        // InternalResoluteParser.g:3020:1: ( RULE_ID )
        {
        // InternalResoluteParser.g:3020:1: ( RULE_ID )
        // InternalResoluteParser.g:3021:1: RULE_ID
        {
        match(input,RULE_ID,FollowSets000.FOLLOW_9); if (state.failed) return ;

        }


        }

        match(input,FullStop,FollowSets000.FOLLOW_10); if (state.failed) return ;
        // InternalResoluteParser.g:3026:1: ( ( RULE_ID ) )
        // InternalResoluteParser.g:3027:1: ( RULE_ID )
        {
        // InternalResoluteParser.g:3027:1: ( RULE_ID )
        // InternalResoluteParser.g:3028:1: RULE_ID
        {
        match(input,RULE_ID,FollowSets000.FOLLOW_14); if (state.failed) return ;

        }


        }

        match(input,LeftParenthesis,FollowSets000.FOLLOW_40); if (state.failed) return ;
        // InternalResoluteParser.g:3033:1: ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )?
        int alt83=2;
        int LA83_0 = input.LA(1);

        if ( ((LA83_0>=Error_state_reachable && LA83_0<=Is_virtual_processor)||(LA83_0>=Flow_specifications && LA83_0<=Is_abstract_feature)||LA83_0==Enumerated_values||(LA83_0>=End_to_end_flows && LA83_0<=Is_bidirectional)||(LA83_0>=Is_thread_group && LA83_0<=Contain_error)||(LA83_0>=Flow_elements && LA83_0<=Is_processor)||LA83_0==Connections||(LA83_0>=Destination && LA83_0<=Upper_bound)||(LA83_0>=Has_member && LA83_0<=Has_parent)||(LA83_0>=Is_of_type && LA83_0<=Is_process)||(LA83_0>=Direction && LA83_0<=Is_thread)||LA83_0==Analysis||(LA83_0>=Features && LA83_0<=Property)||LA83_0==As_list||(LA83_0>=Is_data && LA83_0<=Is_port)||LA83_0==Warning||(LA83_0>=Append && LA83_0<=As_set)||(LA83_0>=Exists && LA83_0<=Member)||(LA83_0>=Parent && LA83_0<=Source)||LA83_0==Debug||(LA83_0>=Error && LA83_0<=False)||LA83_0==Union||(LA83_0>=Fail && LA83_0<=Name)||(LA83_0>=Size && LA83_0<=Tail)||(LA83_0>=This && LA83_0<=Type)||(LA83_0>=Let && LA83_0<=Sum)||LA83_0==If||LA83_0==LeftParenthesis||LA83_0==HyphenMinus||LA83_0==LeftSquareBracket||LA83_0==LeftCurlyBracket||LA83_0==RULE_REAL_LIT||LA83_0==RULE_INTEGER_LIT||(LA83_0>=RULE_STRING && LA83_0<=RULE_ID)) ) {
            alt83=1;
        }
        switch (alt83) {
            case 1 :
                // InternalResoluteParser.g:3033:2: ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )*
                {
                // InternalResoluteParser.g:3033:2: ( ( ruleExpr ) )
                // InternalResoluteParser.g:3034:1: ( ruleExpr )
                {
                // InternalResoluteParser.g:3034:1: ( ruleExpr )
                // InternalResoluteParser.g:3035:1: ruleExpr
                {
                pushFollow(FollowSets000.FOLLOW_16);
                ruleExpr();

                state._fsp--;
                if (state.failed) return ;

                }


                }

                // InternalResoluteParser.g:3037:2: ( Comma ( ( ruleExpr ) ) )*
                loop82:
                do {
                    int alt82=2;
                    int LA82_0 = input.LA(1);

                    if ( (LA82_0==Comma) ) {
                        alt82=1;
                    }


                    switch (alt82) {
                	case 1 :
                	    // InternalResoluteParser.g:3038:1: Comma ( ( ruleExpr ) )
                	    {
                	    match(input,Comma,FollowSets000.FOLLOW_12); if (state.failed) return ;
                	    // InternalResoluteParser.g:3039:1: ( ( ruleExpr ) )
                	    // InternalResoluteParser.g:3040:1: ( ruleExpr )
                	    {
                	    // InternalResoluteParser.g:3040:1: ( ruleExpr )
                	    // InternalResoluteParser.g:3041:1: ruleExpr
                	    {
                	    pushFollow(FollowSets000.FOLLOW_16);
                	    ruleExpr();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }


                	    }


                	    }
                	    break;

                	default :
                	    break loop82;
                    }
                } while (true);


                }
                break;

        }

        match(input,RightParenthesis,FollowSets000.FOLLOW_2); if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred10_InternalResoluteParser

    // Delegated rules

    public final boolean synpred4_InternalResoluteParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_InternalResoluteParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_InternalResoluteParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_InternalResoluteParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_InternalResoluteParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_InternalResoluteParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_InternalResoluteParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_InternalResoluteParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_InternalResoluteParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_InternalResoluteParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_InternalResoluteParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_InternalResoluteParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_InternalResoluteParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_InternalResoluteParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_InternalResoluteParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_InternalResoluteParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_InternalResoluteParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_InternalResoluteParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred10_InternalResoluteParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_InternalResoluteParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA49 dfa49 = new DFA49(this);
    protected DFA66 dfa66 = new DFA66(this);
    static final String dfa_1s = "\31\uffff";
    static final String dfa_2s = "\1\uffff\1\20\25\uffff\1\20\1\uffff";
    static final String dfa_3s = "\1\11\1\75\12\uffff\2\11\4\uffff\1\u00b9\4\uffff\1\75\1\uffff";
    static final String dfa_4s = "\2\u00b9\12\uffff\2\u00b9\4\uffff\1\u00b9\4\uffff\1\u00b9\1\uffff";
    static final String dfa_5s = "\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\13\1\15\2\uffff\1\22\1\23\1\1\1\14\1\uffff\1\20\1\16\1\17\1\21\1\uffff\1\12";
    static final String dfa_6s = "\27\uffff\1\0\1\uffff}>";
    static final String[] dfa_7s = {
            "\2\12\2\uffff\2\12\3\uffff\1\12\2\uffff\3\12\3\uffff\7\12\1\uffff\11\12\1\uffff\1\12\1\uffff\6\12\6\uffff\2\12\1\uffff\2\12\3\uffff\10\12\3\uffff\1\12\1\uffff\4\12\2\uffff\1\12\4\uffff\2\12\2\uffff\1\13\1\uffff\2\12\1\uffff\2\11\3\12\2\uffff\2\12\4\uffff\1\12\1\uffff\1\13\1\6\3\uffff\1\12\4\uffff\1\3\1\12\1\13\1\12\2\uffff\2\12\1\uffff\1\2\1\6\1\12\5\uffff\1\16\1\uffff\1\12\7\uffff\1\10\4\uffff\1\17\14\uffff\1\14\2\uffff\1\15\6\uffff\1\5\1\uffff\1\4\1\uffff\1\7\1\1",
            "\1\20\26\uffff\1\20\11\uffff\2\20\12\uffff\1\20\5\uffff\1\20\2\uffff\1\20\2\uffff\1\20\5\uffff\1\20\2\uffff\1\20\5\uffff\1\20\4\uffff\1\20\1\uffff\1\20\6\uffff\5\20\2\uffff\1\20\1\uffff\1\20\1\21\5\20\1\22\1\20\1\uffff\4\20\1\uffff\2\20\2\uffff\1\20\11\uffff\1\20",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\2\24\2\uffff\2\24\3\uffff\1\24\2\uffff\3\24\3\uffff\7\24\1\uffff\11\24\1\uffff\1\24\1\uffff\6\24\6\uffff\2\24\1\uffff\2\24\3\uffff\10\24\3\uffff\1\24\1\uffff\4\24\2\uffff\1\24\4\uffff\2\24\2\uffff\1\24\1\uffff\2\24\1\uffff\5\24\2\uffff\2\24\4\uffff\1\24\1\uffff\2\24\3\uffff\1\24\4\uffff\4\24\2\uffff\2\24\1\uffff\3\24\5\uffff\3\24\7\uffff\1\24\4\uffff\1\24\4\uffff\1\24\7\uffff\1\24\1\23\1\uffff\1\24\6\uffff\1\24\1\uffff\1\24\1\uffff\2\24",
            "\2\25\2\uffff\2\25\3\uffff\1\25\2\uffff\3\25\3\uffff\7\25\1\uffff\11\25\1\uffff\1\25\1\uffff\6\25\6\uffff\2\25\1\uffff\2\25\3\uffff\10\25\3\uffff\1\25\1\uffff\4\25\2\uffff\1\25\4\uffff\2\25\2\uffff\1\25\1\uffff\2\25\1\uffff\5\25\2\uffff\2\25\4\uffff\1\25\1\uffff\2\25\3\uffff\1\25\4\uffff\4\25\2\uffff\2\25\1\uffff\3\25\5\uffff\3\25\7\uffff\1\25\4\uffff\1\25\4\uffff\1\25\7\uffff\1\25\2\uffff\1\25\1\uffff\1\26\4\uffff\1\25\1\uffff\1\25\1\uffff\2\25",
            "",
            "",
            "",
            "",
            "\1\27",
            "",
            "",
            "",
            "",
            "\1\20\26\uffff\1\20\11\uffff\2\20\12\uffff\1\20\5\uffff\1\20\2\uffff\1\20\2\uffff\1\20\5\uffff\1\20\2\uffff\1\20\5\uffff\1\20\4\uffff\1\20\1\uffff\1\20\7\uffff\4\20\2\uffff\1\20\1\uffff\1\20\1\30\5\20\1\uffff\1\20\1\uffff\4\20\1\uffff\2\20\2\uffff\1\20\11\uffff\1\20",
            ""
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final short[] dfa_2 = DFA.unpackEncodedString(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final char[] dfa_4 = DFA.unpackEncodedStringToUnsignedChars(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[] dfa_6 = DFA.unpackEncodedString(dfa_6s);
    static final short[][] dfa_7 = unpackEncodedStringArray(dfa_7s);

    class DFA49 extends DFA {

        public DFA49(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 49;
            this.eot = dfa_1;
            this.eof = dfa_2;
            this.min = dfa_3;
            this.max = dfa_4;
            this.accept = dfa_5;
            this.special = dfa_6;
            this.transition = dfa_7;
        }
        public String getDescription() {
            return "2635:1: ( ( () ( ( ruleQCREF ) ) ) | ( () otherlv_3= This (otherlv_4= FullStop ( (lv_sub_5_0= ruleNestedDotID ) ) )? ) | ( () otherlv_7= Fail ( ( (lv_val_8_0= ruleExpr ) ) | (otherlv_9= AsteriskAsterisk ( (lv_failmsg_10_0= ruleClaimText ) )+ otherlv_11= AsteriskAsterisk ) ) ) | ( () ( (lv_val_13_0= ruleIntegerTerm ) ) ) | ( () ( (lv_val_15_0= ruleRealTerm ) ) ) | ( () ( (lv_val_17_0= ruleBooleanLiteral ) ) ) | ( () ( (lv_val_19_0= ruleStringTerm ) ) ) | ( () otherlv_21= If ( (lv_cond_22_0= ruleExpr ) ) otherlv_23= Then ( (lv_then_24_0= ruleExpr ) ) otherlv_25= Else ( (lv_else_26_0= ruleExpr ) ) ) | ( () ( ( (lv_quant_28_1= Forall | lv_quant_28_2= Exists ) ) ) (otherlv_29= LeftParenthesis ( (lv_args_30_0= ruleArg ) ) otherlv_31= RightParenthesis )+ otherlv_32= FullStop ( (lv_expr_33_0= ruleExpr ) ) ) | ( ( ( () ( ( RULE_ID ) ) FullStop ( ( RULE_ID ) ) LeftParenthesis ( ( ( ruleExpr ) ) ( Comma ( ( ruleExpr ) ) )* )? RightParenthesis ) )=> ( () ( (lv_libName_35_0= RULE_ID ) ) otherlv_36= FullStop ( (lv_fnName_37_0= RULE_ID ) ) otherlv_38= LeftParenthesis ( ( (lv_args_39_0= ruleExpr ) ) (otherlv_40= Comma ( (lv_args_41_0= ruleExpr ) ) )* )? otherlv_42= RightParenthesis ) ) | ( () ( (lv_fn_44_0= ruleBuiltInFn ) ) otherlv_45= LeftParenthesis ( ( (lv_args_46_0= ruleExpr ) ) (otherlv_47= Comma ( (lv_args_48_0= ruleExpr ) ) )* )? otherlv_49= RightParenthesis ) | ( () ( (otherlv_51= RULE_ID ) ) otherlv_52= LeftParenthesis ( ( (lv_args_53_0= ruleExpr ) ) (otherlv_54= Comma ( (lv_args_55_0= ruleExpr ) ) )* )? otherlv_56= RightParenthesis ) | ( () ( (lv_lintStmt_58_0= ruleLintStatement ) ) ) | (otherlv_59= LeftSquareBracket this_Expr_60= ruleExpr ( ( () otherlv_62= For (otherlv_63= LeftParenthesis ( (lv_args_64_0= ruleArg ) ) otherlv_65= RightParenthesis )+ (otherlv_66= VerticalLine ( (lv_filter_67_0= ruleExpr ) ) )? ) | ( () (otherlv_69= Comma ( (lv_exprs_70_0= ruleExpr ) ) )* ) ) otherlv_71= RightSquareBracket ) | (otherlv_72= LeftCurlyBracket this_Expr_73= ruleExpr ( ( () otherlv_75= For (otherlv_76= LeftParenthesis ( (lv_args_77_0= ruleArg ) ) otherlv_78= RightParenthesis )+ (otherlv_79= VerticalLine ( (lv_filter_80_0= ruleExpr ) ) )? ) | ( () (otherlv_82= Comma ( (lv_exprs_83_0= ruleExpr ) ) )* ) ) otherlv_84= RightCurlyBracket ) | ( () otherlv_86= LeftSquareBracket otherlv_87= RightSquareBracket ) | ( () otherlv_89= LeftCurlyBracket otherlv_90= RightCurlyBracket ) | ( () otherlv_92= Let ( (lv_binding_93_0= ruleLetBinding ) ) otherlv_94= Semicolon ( (lv_expr_95_0= ruleExpr ) ) ) | (otherlv_96= LeftParenthesis this_Expr_97= ruleExpr otherlv_98= RightParenthesis ) )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA49_23 = input.LA(1);

                         
                        int index49_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_23==EOF||LA49_23==Instanceof||LA49_23==Andthen||(LA49_23>=Ruleset && LA49_23<=Warning)||LA49_23==Orelse||LA49_23==Check||LA49_23==Error||LA49_23==Prove||LA49_23==Else||LA49_23==Info||LA49_23==Then||LA49_23==And||LA49_23==For||(LA49_23>=LessThanSignEqualsSign && LA49_23<=GreaterThanSignEqualsSign)||LA49_23==Or||LA49_23==PercentSign||(LA49_23>=RightParenthesis && LA49_23<=HyphenMinus)||LA49_23==Solidus||(LA49_23>=Semicolon && LA49_23<=GreaterThanSign)||(LA49_23>=RightSquareBracket && LA49_23<=CircumflexAccent)||LA49_23==RightCurlyBracket||LA49_23==RULE_ID) ) {s = 16;}

                        else if ( (LA49_23==LeftParenthesis) && (synpred10_InternalResoluteParser())) {s = 24;}

                         
                        input.seek(index49_23);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 49, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_8s = "\23\uffff";
    static final String dfa_9s = "\6\uffff\1\15\1\17\1\uffff\1\21\2\uffff\1\15\1\uffff\1\17\3\uffff\1\21";
    static final String dfa_10s = "\1\70\5\uffff\2\125\1\uffff\1\125\2\uffff\1\125\1\uffff\1\125\1\uffff\1\u00b9\1\uffff\1\125";
    static final String dfa_11s = "\1\u00b9\5\uffff\2\u00b9\1\uffff\1\u00a6\2\uffff\1\u00a6\1\uffff\1\u00a6\1\uffff\1\u00b9\1\uffff\1\u00a6";
    static final String dfa_12s = "\1\uffff\1\1\1\2\1\3\1\4\1\5\2\uffff\1\6\1\uffff\1\11\1\12\1\uffff\1\7\1\uffff\1\10\1\uffff\1\13\1\uffff";
    static final String dfa_13s = "\23\uffff}>";
    static final String[] dfa_14s = {
            "\1\3\23\uffff\1\2\13\uffff\1\4\33\uffff\1\13\22\uffff\1\13\25\uffff\1\12\2\uffff\1\10\1\uffff\1\10\7\uffff\1\1\11\uffff\1\6\1\uffff\1\7\1\uffff\1\5\1\11",
            "",
            "",
            "",
            "",
            "",
            "\1\15\74\uffff\1\10\6\uffff\1\15\4\uffff\1\15\2\uffff\1\15\4\uffff\1\15\22\uffff\1\14",
            "\1\17\74\uffff\1\10\6\uffff\1\17\4\uffff\1\17\2\uffff\1\17\4\uffff\1\17\22\uffff\1\16",
            "",
            "\1\21\74\uffff\1\10\1\20\5\uffff\1\21\4\uffff\1\21\2\uffff\1\21\4\uffff\1\21",
            "",
            "",
            "\1\15\74\uffff\1\10\6\uffff\1\15\4\uffff\1\15\2\uffff\1\15\4\uffff\1\15",
            "",
            "\1\17\74\uffff\1\10\6\uffff\1\17\4\uffff\1\17\2\uffff\1\17\4\uffff\1\17",
            "",
            "\1\22",
            "",
            "\1\21\74\uffff\1\10\6\uffff\1\21\4\uffff\1\21\2\uffff\1\21\4\uffff\1\21"
    };

    static final short[] dfa_8 = DFA.unpackEncodedString(dfa_8s);
    static final short[] dfa_9 = DFA.unpackEncodedString(dfa_9s);
    static final char[] dfa_10 = DFA.unpackEncodedStringToUnsignedChars(dfa_10s);
    static final char[] dfa_11 = DFA.unpackEncodedStringToUnsignedChars(dfa_11s);
    static final short[] dfa_12 = DFA.unpackEncodedString(dfa_12s);
    static final short[] dfa_13 = DFA.unpackEncodedString(dfa_13s);
    static final short[][] dfa_14 = unpackEncodedStringArray(dfa_14s);

    class DFA66 extends DFA {

        public DFA66(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 66;
            this.eot = dfa_8;
            this.eof = dfa_9;
            this.min = dfa_10;
            this.max = dfa_11;
            this.accept = dfa_12;
            this.special = dfa_13;
            this.transition = dfa_14;
        }
        public String getDescription() {
            return "5184:1: (this_RecordTerm_0= ruleRecordTerm | this_ReferenceTerm_1= ruleReferenceTerm | this_ComponentClassifierTerm_2= ruleComponentClassifierTerm | this_ComputedTerm_3= ruleComputedTerm | this_StringTerm_4= ruleStringTerm | this_NumericRangeTerm_5= ruleNumericRangeTerm | this_RealTerm_6= ruleRealTerm | this_IntegerTerm_7= ruleIntegerTerm | this_ListTerm_8= ruleListTerm | this_BooleanLiteral_9= ruleBooleanLiteral | this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm )";
        }
    }
 

    
    private static class FollowSets000 {
        public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L,0x0200000000000000L});
        public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x06A05004071B99F0L,0x0E80E20924082807L,0x0200240000002806L});
        public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000080000000000L});
        public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000800000000000L});
        public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000008000000000L});
        public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000020000000000L});
        public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000800000000L});
        public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0200000000000000L});
        public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000002000000000L});
        public static final BitSet FOLLOW_12 = new BitSet(new long[]{0xD81FAFFBF8E46600L,0xE11A19F6984F47F8L,0x035024042101C1D9L});
        public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
        public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000020000000L});
        public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0200000040000000L});
        public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000240000000L});
        public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000002000100000L});
        public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L});
        public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0300000000000000L});
        public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0300000000020000L});
        public static final BitSet FOLLOW_21 = new BitSet(new long[]{0xD85FAFFBF8E46600L,0xE11A19F69A4F47F8L,0x035024042101C1D9L});
        public static final BitSet FOLLOW_22 = new BitSet(new long[]{0xD85FAFFBF8E46600L,0xE11A19F6984F47F8L,0x035024042101C1D9L});
        public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0100000000000000L});
        public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000010000000L});
        public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000400000L});
        public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000000000002L,0x0000040000000000L,0x0000000004000000L});
        public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L,0x0000000000000400L});
        public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x2000000000000002L});
        public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x06A05004071B99F0L,0x0E80E20924082807L,0x0000000000002806L});
        public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000038000B00000L});
        public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000500000000L});
        public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000001090000000L});
        public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000100000000000L});
        public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000040000000L});
        public static final BitSet FOLLOW_35 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000800000000L});
        public static final BitSet FOLLOW_36 = new BitSet(new long[]{0xD81FAFFBF8E46600L,0xE11A19F6984F47F8L,0x035024042103C1D9L});
        public static final BitSet FOLLOW_37 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
        public static final BitSet FOLLOW_38 = new BitSet(new long[]{0x0000000000000000L,0x1000000000000000L});
        public static final BitSet FOLLOW_39 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000820000000L});
        public static final BitSet FOLLOW_40 = new BitSet(new long[]{0xD81FAFFBF8E46600L,0xE11A19F6984F47F8L,0x035024046101C1D9L});
        public static final BitSet FOLLOW_41 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000080200001000L});
        public static final BitSet FOLLOW_42 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000480020000000L});
        public static final BitSet FOLLOW_43 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000080200000000L});
        public static final BitSet FOLLOW_44 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000800200001000L});
        public static final BitSet FOLLOW_45 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000C00020000000L});
        public static final BitSet FOLLOW_46 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000800200000000L});
        public static final BitSet FOLLOW_47 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000004000000000L});
        public static final BitSet FOLLOW_48 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0200000000000000L});
        public static final BitSet FOLLOW_49 = new BitSet(new long[]{0x0000000000000002L,0x0041000000000000L});
        public static final BitSet FOLLOW_50 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000200000000000L});
        public static final BitSet FOLLOW_51 = new BitSet(new long[]{0x0000000000000000L,0x8008000080000000L,0x0000800000000000L});
        public static final BitSet FOLLOW_52 = new BitSet(new long[]{0x0000000000000002L,0x8008000080000000L});
        public static final BitSet FOLLOW_53 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000400200L});
        public static final BitSet FOLLOW_54 = new BitSet(new long[]{0x0100000000000000L,0x0010000001009000L,0x0350040520000080L});
        public static final BitSet FOLLOW_55 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L,0x0000004202000000L});
        public static final BitSet FOLLOW_56 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000004202000000L});
        public static final BitSet FOLLOW_57 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000002000000L});
        public static final BitSet FOLLOW_58 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0200080000000000L});
        public static final BitSet FOLLOW_59 = new BitSet(new long[]{0x0100000000000000L,0x0010000001009000L,0x0350040560000080L});
        public static final BitSet FOLLOW_60 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
        public static final BitSet FOLLOW_61 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000040800000000L});
        public static final BitSet FOLLOW_62 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0040000000000000L});
        public static final BitSet FOLLOW_63 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000080000040000L});
        public static final BitSet FOLLOW_64 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0250000500000000L});
        public static final BitSet FOLLOW_65 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000040000L});
        public static final BitSet FOLLOW_66 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
        public static final BitSet FOLLOW_67 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000008000000L});
        public static final BitSet FOLLOW_68 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
        public static final BitSet FOLLOW_69 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
        public static final BitSet FOLLOW_70 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000080000L});
        public static final BitSet FOLLOW_71 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000080000L});
    }


}