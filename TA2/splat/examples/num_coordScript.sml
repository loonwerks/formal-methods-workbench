open HolKernel Parse boolLib bossLib Splat;

open arithmeticTheory listTheory stringTheory pred_setLib
     FormalLangTheory charsetTheory regexpTheory regexpLib
     numposrepTheory splatTheory;

(*---------------------------------------------------------------------------*)
(* Boilerplate prelude stuff                                                 *)
(*---------------------------------------------------------------------------*)

val _ = numLib.prefer_num();

infix byA;
val op byA = BasicProvers.byA;

val qpat_k_assum = Lib.C qpat_x_assum kall_tac;

fun qspec q th = th |> Q.SPEC q 
fun qspec_arith q th = qspec q th |> SIMP_RULE arith_ss [];

val var_eq_tac = rpt BasicProvers.VAR_EQ_TAC;

val decide = bossLib.DECIDE;
val qdecide = decide o Parse.Term;

val regexp_lang_cat = el 2 (CONJUNCTS regexp_lang_def);
val regexp_lang_or = last (CONJUNCTS regexp_lang_def);

(*---------------------------------------------------------------------------*)
(* Declare simple record and define wellformedness                           *)
(*---------------------------------------------------------------------------*)

val _ = new_theory "num_coord";

val _ = 
 Hol_datatype
   `dms = <| degrees : num ; 
             minutes : num ; 
             seconds : num |>`;

val good_dms_def =
  Define
    `good_dms recd <=>
         0 <= recd.degrees /\ recd.degrees <= 90 /\
         0 <= recd.minutes /\ recd.minutes <= 59 /\
         0 <= recd.seconds /\ recd.seconds <= 5999`;

val enc_degrees_def =
    Define `enc_degrees d = enc 1 d`;

val enc_minutes_def =
    Define `enc_minutes m = enc 1 m`;

val enc_seconds_def =
    Define `enc_seconds s = enc 2 s`;

(*---------------------------------------------------------------------------*)
(* Encode/decode dms elts                                                    *)
(*---------------------------------------------------------------------------*)

val enc_dms_def =
    Define
    `enc_dms recd =
       CONCAT [enc_degrees recd.degrees;
               enc_minutes recd.minutes;
               enc_seconds recd.seconds]`;

val dec_dms_def =
 Define 
  `dec_dms s =
    case s 
     of [dch; mch; sch1; sch2] => 
        SOME <| degrees := dec [dch]; 
                minutes := dec [mch]; 
                seconds := dec [sch1; sch2] |>
      | otherwise => NONE`;
	     
val dec_enc_dms = Q.store_thm
("dec_enc_dms",
 `!m. good_dms m ==> (dec_dms (enc_dms m) = SOME m)`,
 rw_tac list_ss [good_dms_def,dec_dms_def, enc_dms_def,
                 enc_degrees_def, enc_minutes_def, enc_seconds_def]
  >> `(?a. enc 1 m.degrees = [a]) /\
      (?b. enc 1 m.minutes = [b]) /\
      (?c d. enc 2 m.seconds = [c;d])`
      by rw_tac list_ss [splatTheory.enc_bytes]
  >> rw_tac list_ss [fetch "-" "dms_component_equality"]
  >> metis_tac [splatTheory.dec_enc]);

(*
mapshape [1,1,2] [I,I,I] [0,1,2,3];
*)

(*---------------------------------------------------------------------------*)
(* Regexp expressing the interval constraints                                *)
(*---------------------------------------------------------------------------*)

val dms_regexp = Regexp_Type.fromQuote `\i{0,90}\i{0,59}\i{0,5999}`;

val dms_regexp_term = regexpSyntax.mk_regexp dms_regexp;

(*---------------------------------------------------------------------------*)
(* lift to level of msg format                                               *)
(*---------------------------------------------------------------------------*)

val ilem1A = Q.prove
(`!s. s IN regexp_lang 
             (Chset (Charset 0xFFFFFFFFFFFFFFFFw 0x7FFFFFFw 0w 0w))
      <=> 
      (LENGTH s = 1) /\ dec s <= 90`,
 IN_CHARSET_NUM_TAC);

val ilem2A = Q.prove
(`!s. s IN regexp_lang (Chset (Charset 0xFFFFFFFFFFFFFFFw 0w 0w 0w))
       <=> 
      (LENGTH s = 1) /\ dec s <= 59`,
 IN_CHARSET_NUM_TAC);

val ilem3A = Q.prove
(`!s. s IN regexp_lang 
            (Chset 
              (Charset 0xFFFFFFFFFFFFFFFFw 0xFFFFFFFFFFFFFFFFw 
                       0xFFFFFFFFFFFFFFFFw 0xFFFFFFFFFFFFFFFFw))
       <=>
      (LENGTH s = 1) /\ dec s < 256`,
 IN_CHARSET_NUM_TAC);

val ilem4A = Q.prove
(`!s. s IN regexp_lang (Chset (Charset 0x7FFFFFw 0w 0w 0w))
      <=> 
      (LENGTH s = 1) /\ dec s < 23`,
 IN_CHARSET_NUM_TAC);

val ilem5A = Q.prove
(`!s. s IN regexp_lang 
            (Chset 
              (Charset 0xFFFFFFFFFFFFFFFFw 0xFFFFFFFFFFFFw 0w 0w))
      <=> 
     (LENGTH s = 1) /\ dec s < 112`,
 IN_CHARSET_NUM_TAC);

val ilem6A = Q.prove
(`!s. s IN regexp_lang (Chset (Charset 0x800000w 0w 0w 0w))
      <=> 
      (LENGTH s = 1) /\ (dec s = 23)`,
 IN_CHARSET_NUM_TAC);

val ilem3B = Q.prove
(`!s. s IN regexp_lang 
             (Cat
                 (Chset
                    (Charset 0xFFFFFFFFFFFFFFFFw 0xFFFFFFFFFFFFFFFFw
                       0xFFFFFFFFFFFFFFFFw 0xFFFFFFFFFFFFFFFFw))
                 (Chset (Charset 0x7FFFFFw 0w 0w 0w)))
      <=> 
      (LENGTH s = 2) /\ dec s < 5888`,
 rw_tac list_ss [regexp_lang_cat,IN_dot,ilem3A,ilem4A,strlen_eq,EQ_IMP_THM,dec_def]
 >> rw_tac list_ss []
 >- full_simp_tac list_ss [l2n_def,ord_mod_256]
 >- (qexists_tac `[c1]` >> qexists_tac `[c2]`
     >> full_simp_tac list_ss [l2n_def,ord_mod_256,ORD_BOUND]));

val ilem3C = Q.prove
(`!s. s IN regexp_lang 
            (Cat (Chset
                    (Charset 0xFFFFFFFFFFFFFFFFw 0xFFFFFFFFFFFFw 0w 0w))
                    (Chset (Charset 0x800000w 0w 0w 0w)))
      <=> 
      (LENGTH s = 2) /\ 5888 <= dec s /\ dec s < 6000`,
 rw_tac list_ss [regexp_lang_cat,IN_dot,ilem5A,ilem6A,strlen_eq,
		 decide ``x:num < 6000 <=> x <= 5999``,dec_def,EQ_IMP_THM]
  >> full_simp_tac list_ss [l2n_def,ord_mod_256]
  >> (qexists_tac `[c1]` >> qexists_tac `[c2]`
      >> full_simp_tac list_ss [l2n_def, ord_mod_256]
      >> `ORD c1 < 256 /\ ORD c2 < 256` by metis_tac [ORD_BOUND]
      >> `ORD c2 = 23` by decide_tac
      >> decide_tac)
);

val list_len_lem = Q.prove
(`!L1 L2 L3 h1 h2 h3 h4. 
    1 <= LENGTH L1 /\ 1 <= LENGTH L2 /\ 2 <= LENGTH L3 
    ==> 
    ((L1 ++ L2 ++ L3 = [h1;h2;h3;h4])
    <=>
    (L1 = [h1]) /\
    (L2 = [h2]) /\
    (L3 = [h3;h4]))`,
 simp_tac list_ss [EQ_IMP_THM]
  >> map_every Cases_on [`L1`, `L2`, `L3`]
  >> full_simp_tac list_ss [qdecide `2 <= SUC n <=> 0 < n`]
  >> Cases_on `t''` >> full_simp_tac list_ss []
  >> rpt gen_tac
  >> disch_then (fn th => assume_tac th >> assume_tac (Q.AP_TERM `LENGTH` th))
  >> Cases_on `t` >> full_simp_tac list_ss []
  >> Cases_on `t'` >> full_simp_tac list_ss []);

val in_chset_str = Q.prove
(`!s cs. s IN regexp_lang(Chset cs) ==> ?a. s = [a]`,
 rw_tac (list_ss ++ PRED_SET_ss) [regexp_lang_def]
 >> rw_tac list_ss []);
 

val string_in_lang_len = Q.prove
(`!s cs1 cs2 cs3 cs4 cs5 cs6.
     s IN regexp_lang (Chset cs1) dot
         regexp_lang (Chset cs2) dot
	 ((regexp_lang (Chset cs3) dot regexp_lang (Chset cs4))
          UNION
	  (regexp_lang (Chset cs5) dot regexp_lang (Chset cs6)))
	 ==> ?a b c d. s = [a;b;c;d]`,
 rw_tac (list_ss ++ PRED_SET_ss) [IN_dot]
 >> imp_res_tac in_chset_str
 >> rw_tac list_ss []);

val strcat_enc1_in_chset = Q.prove
(`!n cs s L. 
    n < 256 ==>
    (STRCAT (enc 1 n) s IN regexp_lang (Chset cs) dot L
       <=> 
    enc 1 n IN regexp_lang (Chset cs) /\ s IN L)`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
        [regexp_lang_def,EQ_IMP_THM,IN_dot,enc_bytes]
 >> full_simp_tac list_ss []
 >- metis_tac[]
 >- (qexists_tac `STRING (CHR c) ""`
     >> qexists_tac `s`
     >> rw_tac list_ss []
     >> metis_tac[]));

val strcat_enc2_in_chset = Q.prove
(`!n cs1 cs2 s L. 
    n < 65536 ==>
    (STRCAT (enc 2 n) s 
       IN regexp_lang (Chset cs1) dot 
          regexp_lang (Chset cs2) dot L
       <=> 
    enc 2 n IN regexp_lang (Chset cs1) dot regexp_lang (Chset cs2) 
    /\ s IN L)`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
        [regexp_lang_def,EQ_IMP_THM,IN_dot,enc_bytes]
 >> full_simp_tac list_ss []
 >- (qexists_tac `[CHR c]`
     >> qexists_tac `[CHR c']`
     >> rw_tac list_ss []
     >> metis_tac [])
 >- (qexists_tac `[CHR c]`
     >> qexists_tac `CHR c' :: s`
     >> rw_tac list_ss []
     >- metis_tac []
     >- metis_tac [STRCAT_EQNS])
);

val AGREE_PROP = Q.store_thm
("AGREE_PROP",
 `!m:dms. good_dms m <=> enc_dms(m) IN regexp_lang ^dms_regexp_term`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
	   [regexp_lang_cat,regexp_lang_or,LIST_UNION_def,
            enc_dms_def, enc_degrees_def, enc_minutes_def, enc_seconds_def,EQ_IMP_THM]
 >> full_simp_tac bool_ss [Once (GSYM STRCAT_ASSOC)]
 >- (full_simp_tac bool_ss [good_dms_def]
     >> rw_tac list_ss [strcat_enc1_in_chset]  (* split into enc-level proofs *)
     >- (rw_tac list_ss [ilem1A,dec_enc] >> rw_tac list_ss [enc_bytes])
     >- (rw_tac list_ss [ilem2A,dec_enc] >> rw_tac list_ss [enc_bytes])
     >- (rw_tac list_ss [ilem3B |> SIMP_RULE list_ss [regexp_lang_cat],
                         ilem3C |> SIMP_RULE list_ss [regexp_lang_cat],dec_enc]
         >> rw_tac list_ss [enc_bytes]))
 >- (full_simp_tac list_ss 
         [regexp_lang_or,regexp_lang_cat,regexpTheory.LIST_UNION_def,IN_dot,
	  ilem1A,ilem2A,ilem3A,ilem4A,ilem5A,ilem6A,ilem3B,ilem3C,strlen_eq]
     >> var_eq_tac
     >> full_simp_tac list_ss [list_len_lem,lower_enc]
     >> `m.degrees <= 90` by metis_tac [dec_enc,good_dms_def]
     >> `m.minutes <= 59` by metis_tac [dec_enc,good_dms_def]
     >> `m.seconds = dec (STRING c'' (STRING c''' ""))` by metis_tac [dec_enc]
     >> full_simp_tac list_ss [dec_def, l2n_def,ord_mod_256,good_dms_def])
);


val good_imp_accept_tac =
 full_simp_tac bool_ss [good_dms_def]
 >> rw_tac list_ss [strcat_enc1_in_chset,ilem1A, ilem2A,  
         ilem3B |> SIMP_RULE list_ss [regexp_lang_cat],
         ilem3C |> SIMP_RULE list_ss [regexp_lang_cat],dec_enc,enc_bytes]

val AGREE_PROP_AGAIN = Q.store_thm
("AGREE_PROP_AGAIN",
 `!m:dms. good_dms m <=> enc_dms(m) IN regexp_lang ^dms_regexp_term`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
	   [regexp_lang_cat,regexp_lang_or,LIST_UNION_def,
            enc_dms_def, enc_degrees_def, enc_minutes_def, enc_seconds_def,EQ_IMP_THM]
 >> full_simp_tac bool_ss [Once (GSYM STRCAT_ASSOC)]
 >- good_imp_accept_tac
 >- (full_simp_tac list_ss 
         [regexp_lang_or,regexp_lang_cat,regexpTheory.LIST_UNION_def,IN_dot,
	  ilem1A,ilem2A,ilem3A,ilem4A,ilem5A,ilem6A,ilem3B,ilem3C,strlen_eq]
     >> var_eq_tac
     >> full_simp_tac list_ss [list_len_lem,lower_enc]
     >> `m.degrees <= 90` by metis_tac [dec_enc,good_dms_def]
     >> `m.minutes <= 59` by metis_tac [dec_enc,good_dms_def]
     >> `m.seconds = dec (STRING c'' (STRING c''' ""))` by metis_tac [dec_enc]
     >> full_simp_tac list_ss [dec_def, l2n_def,ord_mod_256,good_dms_def])
);

val in_chset = Q.prove
(`!n cs s L . STRING c s IN regexp_lang (Chset cs) dot L
              <=> 
             (STRING c "") IN regexp_lang (Chset cs) /\ s IN L`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
        [regexp_lang_def,EQ_IMP_THM,IN_dot]
 >> full_simp_tac list_ss []
 >> var_eq_tac
 >- metis_tac[]
 >- (qexists_tac `STRING (CHR c') ""`
     >> qexists_tac `s`
     >> rw_tac list_ss []
     >> metis_tac[]));

val list_len_lemA = Q.prove
(`!L1 L2 L3 h1 h2 h3 h4. 
    1 <= LENGTH L1 /\ 1 <= LENGTH L2 /\ 2 <= LENGTH L3 
    ==> 
    ((L1 ++ L2 ++ L3 = [h1;h2;h3;h4])
    <=>
    ([h1] = L1) /\
    ([h2] = L2) /\
    ([h3;h4] = L3))`,
 metis_tac [list_len_lem]);


val AGREE_PROP_AGAIN_A = Q.store_thm
("AGREE_PROP_AGAIN_A",
 `!m:dms. good_dms m <=> enc_dms(m) IN regexp_lang ^dms_regexp_term`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
	   [regexp_lang_cat,regexp_lang_or,LIST_UNION_def,
            enc_dms_def, enc_degrees_def, enc_minutes_def, enc_seconds_def,EQ_IMP_THM]
 >> full_simp_tac bool_ss [Once (GSYM STRCAT_ASSOC)]
 >- good_imp_accept_tac
 >- (imp_res_tac string_in_lang_len
     >> full_simp_tac list_ss [in_chset]
     >> full_simp_tac list_ss [list_len_lemA,lower_enc]
     >> full_simp_tac list_ss [ilem1A,ilem2A,ilem3A,ilem4A,ilem5A,ilem6A]
     >> `m.degrees <= 90 /\ m.minutes <= 59` by metis_tac [dec_enc,good_dms_def]
     >> `m.seconds = dec (STRING c (STRING d ""))` by metis_tac [dec_enc]
     >> full_simp_tac list_ss [dec_def, l2n_def,ord_mod_256,good_dms_def])
);

val AGREE_PROP_AGAIN_B = Q.store_thm
("AGREE_PROP_AGAIN_B",
 `!m:dms. good_dms m <=> enc_dms(m) IN regexp_lang ^dms_regexp_term`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
	   [regexp_lang_cat,regexp_lang_or,LIST_UNION_def,
            enc_dms_def, enc_degrees_def, enc_minutes_def, enc_seconds_def,EQ_IMP_THM]
 >> full_simp_tac bool_ss [Once (GSYM STRCAT_ASSOC)]
 >- good_imp_accept_tac
 >- (imp_res_tac string_in_lang_len
     >> full_simp_tac list_ss [in_chset]
     >> full_simp_tac list_ss [list_len_lemA,lower_enc]
     >> full_simp_tac list_ss [ilem1A,ilem2A,ilem3A,ilem4A,ilem5A,ilem6A]
     >> `m.degrees <= 90 /\ m.minutes <= 59` by metis_tac [dec_enc,good_dms_def]
     >> `m.seconds = dec (STRING c (STRING d ""))` by metis_tac [dec_enc]
     >> full_simp_tac list_ss [dec_def, l2n_def,ord_mod_256,good_dms_def])
);

val _ = export_theory();
 
