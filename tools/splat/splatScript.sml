(*===========================================================================*)
(* Load and open required context                                            *)
(*===========================================================================*)

open HolKernel Parse boolLib bossLib BasicProvers;

open arithmeticTheory listTheory stringTheory 
     ASCIInumbersTheory numposrepTheory ASCIInumbersLib integerTheory;

val int_ss = intLib.int_ss;

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

(*---------------------------------------------------------------------------*)
(* Let's get started                                                         *)
(*---------------------------------------------------------------------------*)

val _ = new_theory "splat";

(*---------------------------------------------------------------------------*)
(* Definitions                                                               *)
(*---------------------------------------------------------------------------*)

val layout_def =  (* LSB with padding to width *)
 Define
  `layout b n width = PAD_RIGHT 0n width (n2l b n)`;

val enc_def = Define `enc w n = MAP CHR (layout 256 n w)`;
val dec_def = Define `dec s = l2n 256 (MAP ORD s)`;

val n2l_256 = save_thm
("n2l_256",
 n2l_def 
  |> Q.SPECL [`n`,`256`] 
  |> SIMP_RULE arith_ss []
  |> Q.GEN `n`
);

val ord_mod_256 = Q.store_thm
("ord_mod_256",
 `!c. ORD c MOD 256 = ORD c`,
 rw_tac arith_ss [ORD_BOUND]);

val MAP_ORD_CHR = Q.prove
(`!list. EVERY ($> 256) list ==> (MAP (ORD o CHR) list = list)`,
 Induct >> rw_tac list_ss [ORD_CHR_RWT]);

val l2n_append_zeros = Q.prove
(`!n list. l2n 256 (list ++ GENLIST (K 0) n) = l2n 256 list`,
Induct 
 >> rw_tac list_ss [GENLIST]
 >> metis_tac [APPEND_SNOC, qspec_arith `256` l2n_SNOC_0]);

(*---------------------------------------------------------------------------*)
(* Invertibility for primitive encoder/decoder                               *)
(*---------------------------------------------------------------------------*)

val dec_enc = Q.store_thm
("dec_enc",
 `!n w. dec (enc w n) = n`,
 rw_tac list_ss [enc_def, dec_def,layout_def,MAP_MAP_o,
    PAD_RIGHT,n2l_BOUND,EVERY_GENLIST,MAP_ORD_CHR,l2n_append_zeros,l2n_n2l]);

(*---------------------------------------------------------------------------*)
(* Unrolling n2l a fixed number of times.                                    *)
(*---------------------------------------------------------------------------*)

val n2l_bytes_1 = Q.prove
(`!n. n < 256 ==> (n2l 256 n = [n])`,
 rw_tac list_ss [Once n2l_256]);

val n2l_bytes_2 = Q.prove
(`!n. ~(n < 256) /\ n < 65536 ==> 
       (n2l 256 n = [n MOD 256; (n DIV 256) MOD 256])`,
 rw_tac list_ss 
    [Ntimes n2l_256 2, 
     arithmeticTheory.DIV_DIV_DIV_MULT,arithmeticTheory.DIV_LT_X]);

val n2l_bytes_3 = Q.prove
(`!n. ~(n < 65536) /\ n < 256 * 65536 ==> 
       (n2l 256 n = [n MOD 256; 
                     (n DIV 256) MOD 256;
		     (n DIV 65536) MOD 256])`,
 rw_tac list_ss 
    [Ntimes n2l_256 3, 
     arithmeticTheory.DIV_DIV_DIV_MULT,arithmeticTheory.DIV_LT_X]);

val n2l_bytes_4 = Q.prove
(`!n. ~(n < 16777216) /\ n < 256 * 16777216 ==> 
       (n2l 256 n = [n MOD 256; 
                     (n DIV 256) MOD 256;
		     (n DIV 65536) MOD 256;
		     (n DIV 16777216) MOD 256])`,
 rw_tac list_ss 
    [Ntimes n2l_256 4, 
     arithmeticTheory.DIV_DIV_DIV_MULT,arithmeticTheory.DIV_LT_X]);

(* Table of powers of 256 

   2 -- 65536
   3 -- 16777216
   4 -- 4294967296
   5 -- 1099511627776
   6 -- 281474976710656
   7 -- 72057594037927936
   8 -- 18446744073709551616
*)

val n2l_bytes_5 = Q.prove
(`!n. ~(n < 4294967296) /\ n < 1099511627776 ==> 
       (n2l 256 n = [n MOD 256; 
                     (n DIV 256) MOD 256;
		     (n DIV 65536) MOD 256;
		     (n DIV 16777216) MOD 256;
                     (n DIV 4294967296) MOD 256])`,
 rw_tac list_ss 
    [Ntimes n2l_256 5, 
     arithmeticTheory.DIV_DIV_DIV_MULT,arithmeticTheory.DIV_LT_X]);

val n2l_bytes_6 = Q.prove
(`!n. ~(n < 1099511627776) /\ n < 281474976710656 ==> 
       (n2l 256 n = [n MOD 256; 
                     (n DIV 256) MOD 256;
		     (n DIV 65536) MOD 256;
		     (n DIV 16777216) MOD 256;
                     (n DIV 4294967296) MOD 256;
                     (n DIV 1099511627776) MOD 256])`,
 rw_tac list_ss 
    [Ntimes n2l_256 6, 
     arithmeticTheory.DIV_DIV_DIV_MULT,arithmeticTheory.DIV_LT_X]);

val n2l_bytes_7 = Q.prove
(`!n. ~(n < 281474976710656) /\ n < 72057594037927936 ==> 
       (n2l 256 n = [n MOD 256; 
                     (n DIV 256) MOD 256;
		     (n DIV 65536) MOD 256;
		     (n DIV 16777216) MOD 256;
                     (n DIV 4294967296) MOD 256;
                     (n DIV 1099511627776) MOD 256;
                     (n DIV 281474976710656) MOD 256])`,
 rw_tac list_ss 
    [Ntimes n2l_256 7, 
     arithmeticTheory.DIV_DIV_DIV_MULT,arithmeticTheory.DIV_LT_X]);

val n2l_bytes_8 = Q.prove
(`!n. ~(n < 72057594037927936) /\ n < 18446744073709551616 ==> 
       (n2l 256 n = [n MOD 256; 
                     (n DIV 256) MOD 256;
		     (n DIV 65536) MOD 256;
		     (n DIV 16777216) MOD 256;
                     (n DIV 4294967296) MOD 256;
                     (n DIV 1099511627776) MOD 256;
                     (n DIV 281474976710656) MOD 256;
                     (n DIV 72057594037927936) MOD 256])`,
 rw_tac list_ss 
    [Ntimes n2l_256 8, 
     arithmeticTheory.DIV_DIV_DIV_MULT,arithmeticTheory.DIV_LT_X]);

val n2l_bytes = save_thm
("n2l_bytes",
 LIST_CONJ [n2l_bytes_1,n2l_bytes_2,n2l_bytes_3,n2l_bytes_4,
            n2l_bytes_5,n2l_bytes_6,n2l_bytes_7,n2l_bytes_8]);

(*---------------------------------------------------------------------------*)
(* Encoding nums in expected ranges                                          *)
(*---------------------------------------------------------------------------*)

val enc_1_lem = Q.prove
(`!n. n < 256 ==> (enc 1 n = STRING (CHR n) "")`,
 rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def,n2l_bytes_1]);


val enc_2_lem = Q.prove
(`!n. n < 256 * 256 ==> 
      (enc 2 n = STRING (CHR (n MOD 256)) 
                (STRING (CHR ((n DIV 256) MOD 256)) ""))`,
rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def,n2l_bytes_2]
 >> Cases_on `n < 256`
 >- rw_tac list_ss [n2l_bytes_1,arithmeticTheory.LESS_DIV_EQ_ZERO]
 >- rw_tac list_ss [n2l_bytes_2]);

val enc_3_lem = Q.prove
(`!n. n < 256 * 256 * 256 ==> 
      (enc 3 n = STRING (CHR (n MOD 256)) 
                (STRING (CHR ((n DIV 256) MOD 256)) 
                (STRING (CHR ((n DIV 65536) MOD 256)) "")))`,
rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def]
 >> Cases_on `n < 256`
 >- rw_tac list_ss [n2l_bytes_1,arithmeticTheory.LESS_DIV_EQ_ZERO]
 >- (Cases_on `n < 65536`
     >- rw_tac list_ss [n2l_bytes_2,arithmeticTheory.LESS_DIV_EQ_ZERO]
     >- rw_tac list_ss [n2l_bytes_3]));

val enc_4_lem = Q.prove
(`!n. n < 256 * 256 * 256 * 256 ==> 
      (enc 4 n = STRING (CHR (n MOD 256)) 
                (STRING (CHR ((n DIV 256) MOD 256)) 
                (STRING (CHR ((n DIV 65536) MOD 256))
		(STRING (CHR ((n DIV 16777216) MOD 256)) ""))))`,
rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def]
 >> Cases_on `n < 256`
 >- rw_tac list_ss [n2l_bytes_1,arithmeticTheory.LESS_DIV_EQ_ZERO]
 >- (Cases_on `n < 65536`
     >- rw_tac list_ss [n2l_bytes_2,arithmeticTheory.LESS_DIV_EQ_ZERO]
     >- (Cases_on `n < 16777216`
         >- rw_tac list_ss [n2l_bytes_3,arithmeticTheory.LESS_DIV_EQ_ZERO]
         >- rw_tac list_ss [n2l_bytes_4])));

val enc_5_lem = Q.prove
(`!n. n < 256 * 256 * 256 * 256 * 256 ==> 
      (enc 5 n = STRING (CHR (n MOD 256)) 
                (STRING (CHR ((n DIV 256) MOD 256)) 
                (STRING (CHR ((n DIV 65536) MOD 256))
		(STRING (CHR ((n DIV 16777216) MOD 256))
		(STRING (CHR ((n DIV 4294967296) MOD 256))
                "")))))`,
rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def]
 >> Cases_on `n < 256`
 >- rw_tac list_ss [n2l_bytes_1,LESS_DIV_EQ_ZERO]
 >- (Cases_on `n < 65536`
     >- rw_tac list_ss [n2l_bytes_2,LESS_DIV_EQ_ZERO]
     >- (Cases_on `n < 16777216`
         >- rw_tac list_ss [n2l_bytes_3,LESS_DIV_EQ_ZERO]
         >- (Cases_on `n < 4294967296`
             >- rw_tac list_ss [n2l_bytes_4,LESS_DIV_EQ_ZERO]
             >- rw_tac list_ss [n2l_bytes_5]))))
;

val enc_6_lem = Q.prove
(`!n. n < 256 * 256 * 256 * 256 * 256 * 256 ==> 
      (enc 6 n = STRING (CHR (n MOD 256)) 
                (STRING (CHR ((n DIV 256) MOD 256)) 
                (STRING (CHR ((n DIV 65536) MOD 256))
		(STRING (CHR ((n DIV 16777216) MOD 256))
		(STRING (CHR ((n DIV 4294967296) MOD 256))
		(STRING (CHR ((n DIV 1099511627776) MOD 256))
                ""))))))`,
rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def]
 >> Cases_on `n < 256`
 >- rw_tac list_ss [n2l_bytes_1,LESS_DIV_EQ_ZERO]
 >- (Cases_on `n < 65536`
     >- rw_tac list_ss [n2l_bytes_2,LESS_DIV_EQ_ZERO]
     >- (Cases_on `n < 16777216`
         >- rw_tac list_ss [n2l_bytes_3,LESS_DIV_EQ_ZERO]
         >- (Cases_on `n < 4294967296`
             >- rw_tac list_ss [n2l_bytes_4,LESS_DIV_EQ_ZERO]
             >- (Cases_on `n < 1099511627776`
                 >- rw_tac list_ss [n2l_bytes_5,LESS_DIV_EQ_ZERO]
                 >- rw_tac list_ss [n2l_bytes_6])))))
;

val enc_7_lem = Q.prove
(`!n. n < 256 * 256 * 256 * 256 * 256 * 256 * 256 ==> 
      (enc 7 n = STRING (CHR (n MOD 256)) 
                (STRING (CHR ((n DIV 256) MOD 256)) 
                (STRING (CHR ((n DIV 65536) MOD 256))
		(STRING (CHR ((n DIV 16777216) MOD 256))
		(STRING (CHR ((n DIV 4294967296) MOD 256))
		(STRING (CHR ((n DIV 1099511627776) MOD 256))
		(STRING (CHR ((n DIV 281474976710656) MOD 256))
                "")))))))`,
rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def]
 >> Cases_on `n < 256`
 >- rw_tac list_ss [n2l_bytes_1,LESS_DIV_EQ_ZERO]
 >- (Cases_on `n < 65536`
     >- rw_tac list_ss [n2l_bytes_2,LESS_DIV_EQ_ZERO]
     >- (Cases_on `n < 16777216`
         >- rw_tac list_ss [n2l_bytes_3,LESS_DIV_EQ_ZERO]
         >- (Cases_on `n < 4294967296`
             >- rw_tac list_ss [n2l_bytes_4,LESS_DIV_EQ_ZERO]
             >- (Cases_on `n < 1099511627776`
                 >- rw_tac list_ss [n2l_bytes_5,LESS_DIV_EQ_ZERO]
                 >- (Cases_on `n < 281474976710656`
                     >- rw_tac list_ss [n2l_bytes_6,LESS_DIV_EQ_ZERO]
                     >- rw_tac list_ss [n2l_bytes_7]))))))
;

val enc_8_lem = Q.prove
(`!n. n < 256 * 256 * 256 * 256 * 256 * 256 * 256 * 256 ==> 
      (enc 8 n = STRING (CHR (n MOD 256)) 
                (STRING (CHR ((n DIV 256) MOD 256)) 
                (STRING (CHR ((n DIV 65536) MOD 256))
		(STRING (CHR ((n DIV 16777216) MOD 256))
		(STRING (CHR ((n DIV 4294967296) MOD 256))
		(STRING (CHR ((n DIV 1099511627776) MOD 256))
		(STRING (CHR ((n DIV 281474976710656) MOD 256))
		(STRING (CHR ((n DIV 72057594037927936) MOD 256)) 
                ""))))))))`,
rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def]
 >> Cases_on `n < 256`
 >- rw_tac list_ss [n2l_bytes_1,LESS_DIV_EQ_ZERO]
 >- (Cases_on `n < 65536`
     >- rw_tac list_ss [n2l_bytes_2,LESS_DIV_EQ_ZERO]
     >- (Cases_on `n < 16777216`
         >- rw_tac list_ss [n2l_bytes_3,LESS_DIV_EQ_ZERO]
         >- (Cases_on `n < 4294967296`
             >- rw_tac list_ss [n2l_bytes_4,LESS_DIV_EQ_ZERO]
             >- (Cases_on `n < 1099511627776`
                 >- rw_tac list_ss [n2l_bytes_5,LESS_DIV_EQ_ZERO]
                 >- (Cases_on `n < 281474976710656`
                     >- rw_tac list_ss [n2l_bytes_6,LESS_DIV_EQ_ZERO]
                     >- (Cases_on `n < 72057594037927936`
                         >- rw_tac list_ss [n2l_bytes_7,LESS_DIV_EQ_ZERO]
                         >- rw_tac list_ss [n2l_bytes_8])))))))
;

val enc_bytes = save_thm
("enc_bytes",
 LIST_CONJ [enc_1_lem,enc_2_lem,enc_3_lem,enc_4_lem,
            enc_5_lem,enc_6_lem,enc_7_lem,enc_8_lem]);

(*---------------------------------------------------------------------------*)
(* Lower bounds on encoding lengths                                          *)
(*---------------------------------------------------------------------------*)

Theorem lower_enc :
  !w n. w <= LENGTH (enc w n)
Proof
  rw_tac list_ss [enc_def,layout_def, PAD_RIGHT]
QED

(*---------------------------------------------------------------------------*)
(* Mapping from string lengths to character lists with abstract contents     *)
(*---------------------------------------------------------------------------*)

val STRLEN_EQ_1 = Q.prove
(`!s. (STRLEN s = 1) <=> ?c. s = STRING c ""`,
 Induct >> rw_tac list_ss []);

val STRLEN_EQ_2 = Q.prove
(`!s. (STRLEN s = 2) <=> ?c1 c2. s = STRING c1 (STRING c2 "")`,
 Cases >> rw_tac list_ss []
       >> Cases_on `t` >> rw_tac list_ss []);

val STRLEN_EQ_3 = Q.prove 
(`!s. (STRLEN s = 3) <=> ?c1 c2 c3. s = [c1; c2 ; c3]`, 
 Cases >> rw_tac list_ss [] 
       >> Cases_on `t` >> rw_tac list_ss []
       >> Cases_on `t'` >> rw_tac list_ss [] 
       >> Cases_on `t` >> rw_tac list_ss []);

val strlen_eq = save_thm
("strlen_eq",
 LIST_CONJ [STRLEN_EQ_1,STRLEN_EQ_2,STRLEN_EQ_3]);

(*---------------------------------------------------------------------------*)
(* Signed numbers                                                            *)
(*                                                                           *)
(* At the word level, 2s complement is "flip each bit and add 1". But it is  *)
(* also understood purely at the level of ints and nums since an n-bit       *)
(* integer added to its 2s complement equals 2^n (with appropriate coercions *)
(* tossed in to map back and forth between ints and nums). Thus              *)
(*                                                                           *)
(*   twos_comp : {i | -2^{N-1} <= i < 2^{N-1}} -> {k | 0 <= k < 2^{N}}       *)
(*                                                                           *)
(* maps an integer into its corresponding natural number and is defined by   *)
(*                                                                           *)
(*   twos_comp(i) = if 0 <= i < 2^{N-1} then Nat(i) else Nat(2^{N} + i)      *)
(*                                                                           *)
(* To round-trip encode-then-decode integer i, we map i to n:num by using    *)
(* 2s complement. Then we use l2n o n2l to get back out to 2s complement     *)
(* num, and then we reverse the 2s complement map.                           *)
(*                                                                           *)
(*---------------------------------------------------------------------------*)
(*
 ML version:

  fun n2l n = if n < 256 then [n] else (n mod 256) :: n2l (n div 256);

  fun l2n [] = 0
    | l2n (h::t) = h + 256 * l2n t;

  fun i2n top i = if 0 <= i then i else top + i;

  fun n2i top top_div_2 n = 
    if n < top_div_2 then n 
    else ~(top - n);

  fun ntest n = (n = l2n (n2l n));
  List.all (equal true) (map ntest (upto 0 65537));

  fun itest bits = 
    let open MiscLib 
        val top = Arbint.toInt (twoE bits)
        val n2i = n2i top (top div 2)
        val i2n = i2n top
   in 
      fn i => (i = n2i (l2n (n2l (i2n i))))
   end;

  List.all (equal true) (map (itest 16) (upto ~32768 32767));
*)

val i2n_def =
 Define
  `i2n bits i =
      if 0i <= i then Num(i) else (2 ** bits) - Num(ABS(i))`;

val n2i_def =
 Define
  `n2i bits n = 
     if n < 2 ** (bits - 1n)
      then int_of_num n
     else  -(&(2 ** bits - n))`;

(*
EVAL ``i2n 8n 13i``;
EVAL ``i2n 8n (-13i)``;

EVAL ``n2i 8n (i2n 8n 13i)``;
EVAL ``n2i 8n (i2n 8n (-13i))``;
*)

val lem = Q.prove
(`!x i:int. i < 0 /\ x < Num (ABS i) /\ -(&x) <= i ==> F`,
 rw_tac int_ss [INT_ABS] >> intLib.ARITH_TAC);

val n2i_i2n = Q.store_thm
("n2i_i2n",
 `!bits i. 
    0n < bits /\
   -(&(2n ** (bits - 1))) <= i /\ i < &(2n ** (bits - 1))
  ==>
    (n2i bits (i2n bits i) = i)`,
 rw_tac int_ss [n2i_def, i2n_def]
 >> full_simp_tac int_ss [INT_OF_NUM]
 >- (rw_tac int_ss [INT_ABS]
     >- (qpat_x_assum `~(0 <= i)` kall_tac
         >> qpat_x_assum `i < &(2 ** (bits - 1))` kall_tac
         >> `2n ** bits = 2n ** (bits - 1) + 2n ** (bits - 1)`
             by (Cases_on `bits` >> full_simp_tac arith_ss [EXP])
         >> pop_assum SUBST_ALL_TAC
         >> full_simp_tac arith_ss []
         >> metis_tac [lem])
     >- metis_tac [intLib.ARITH_PROVE ``~(0 <= (i:int)) ==> ~(i < 0) ==> F``])
 >- intLib.ARITH_TAC
 >- intLib.ARITH_TAC
);


val enci_def = Define `enci w i = enc w (i2n (8*w) i)`;
val deci_def = Define `deci w s = n2i (8*w) (dec s)`;

val deci_enci = Q.store_thm
("deci_enci",
 `!w i. 0 < w /\ -(&(2n ** ((8 * w) - 1))) <= i /\ i < &(2n ** ((8 * w) - 1))
        ==> (deci w (enci w i) = i)`,
 rw_tac int_ss [enci_def, deci_def,dec_enc,n2i_i2n]);

val deci_encis = save_thm
("deci_encis",
 LIST_CONJ
     (map (C qspec_arith deci_enci) [`1`,`2`,`3`,`4`,`5`,`6`,`7`,`8`]));

val i2n_bounds = Q.prove
(`!bits i. -(int_of_num(2 ** (bits-1))) <= i /\ 
        i < (int_of_num (2 ** (bits-1))) ==> i2n bits i < 2 ** bits`,
 Cases >> rw_tac int_ss [i2n_def,EXP] >> intLib.ARITH_TAC);

val i2n_bounds_1 = Q.prove
(`!i:int. -128i <= i /\ i < 128 ==> i2n 8 i < 256n`,
 metis_tac [qspec_arith `8` i2n_bounds]);

val i2n_bounds_2 = Q.prove
(`!i:int. -32768i <= i /\ i < 32768 ==> i2n 16 i < 65536n`,
 metis_tac [qspec_arith `16` i2n_bounds]);

val i2n_bounds_3 = Q.prove
(`!i:int. -8388608i <= i /\ i < 8388608 ==> i2n 24 i < 16777216n`,
 metis_tac [qspec_arith `24` i2n_bounds]);

val i2n_bounds_4 = Q.prove
(`!i:int. -2147483648i <= i /\ i < 2147483648i ==> i2n 32 i < 4294967296`,
 metis_tac [qspec_arith `32` i2n_bounds]);

val i2n_bounds_8 = Q.prove
(`!i:int. -9223372036854775808i <= i /\ i < 9223372036854775808i 
    ==> 
   i2n 64 i < 18446744073709551616n`,
 metis_tac [qspec_arith `64` i2n_bounds]);

val enc_bytes' = SIMP_RULE arith_ss [] enc_bytes;

val enci_byte_1 = Q.prove
(`!i:int. -128i <= i /\ i < 128i ==> ?a. enci 1 i = [a]`,
 rw_tac list_ss [enci_def]
 >> metis_tac [enc_bytes',i2n_bounds_1]);

val enci_byte_2 = Q.prove
(`!i:int. -32768i <= i /\ i < 32768 ==> ?a b. enci 2 i = [a;b]`,
 rw_tac list_ss [enci_def]
 >> metis_tac [enc_bytes',i2n_bounds_2]);

val enci_byte_3 = Q.prove
(`!i:int. -8388608i <= i /\ i < 8388608 ==> ?a b c. enci 3 i = [a;b;c]`,
 rw_tac list_ss [enci_def]
 >> metis_tac [enc_bytes',i2n_bounds_3]);

val enci_byte_4 = Q.prove
(`!i:int. -2147483648i <= i /\ i < 2147483648i ==> ?a b c d. enci 4 i = [a;b;c;d]`,
 rw_tac list_ss [enci_def]
 >> metis_tac [enc_bytes',i2n_bounds_4]);

val enci_byte_8 = Q.prove
(`!i:int. -9223372036854775808i <= i /\ i < 9223372036854775808i 
        ==> ?a b c d e f g h. enci 8 i = [a;b;c;d;e;f;g;h]`,
 rw_tac list_ss [enci_def]
 >> metis_tac [enc_bytes',i2n_bounds_8]);

val enci_bytes = save_thm
 ("enci_bytes",
  LIST_CONJ [enci_byte_1,enci_byte_2,enci_byte_3,enci_byte_4,enci_byte_8]);

val lower_enci = Q.store_thm
("lower_enci",
 `!w i. w <= LENGTH (enci w i)`,
 rw_tac list_ss [enci_def,lower_enc]);


(*---------------------------------------------------------------------------*)
(* enci has a restricted domain. Revise to have unrestricted.                *)
(*---------------------------------------------------------------------------*)

(*---------------------------------------------------------------------------*)
(* Width needed to encode number n in base 256                               *)
(*---------------------------------------------------------------------------*)

val bitwidthN_def = 
 Define 
  `bitwidthN n = if n = 0 then 1 else SUC (LOG 2 n)`;

val bytewidthN_def = 
 Define 
  `bytewidthN n = if n = 0 then 1 else SUC (LOG 256 n)`;

(*
fun sbit_width i =
 let open Arbint
     fun W bits =
       let val N = twoE (Int.-(bits,1))
       in if Arbint.~(N) <= i andalso i < N then bits else W (Int.+(bits,1))
       end
 in W 0
 end;

val bits2bytes = 
 let fun roundup (q,r) = q + (if r > 0 then 1 else 0)
 in fn n => roundup(n div 8,n mod 8)
 end

fun sbyte_width i = bits2bytes(sbit_width i)
*)

val NWIDTH_def = Define `NWIDTH n = if n=0 then 1 else 1 + LOG 256 n`;

val nwidth_pos = Q.prove
(`!n. 0 < NWIDTH n`,
 rw_tac arith_ss [NWIDTH_def]);

open logrootTheory;

val LOG_EXACT_RWT = Q.prove
(`!b n. 1 < b /\ 0 < n ==> (LOG b (b ** n) = n)`,
 rpt strip_tac
  >> match_mp_tac LOG_UNIQUE
  >> rw_tac arith_ss [EXP]);

val LOG_LESS = Q.prove
(`!n j. 0 < j /\ 0 < n /\ n < 256 ** j ==> LOG 256 n < j`,
 rpt strip_tac
 >> strip_assume_tac 
     (qdecide `!a b:num. a < b \/ (a = b) \/ b < a`
         |> qspec `LOG 256 n`
         |> qspec `j:num`)
  >- (mp_tac (LOG |> qspec_arith `256` |> qspec `n`)
      >> rw_tac arith_ss [])
  >- (mp_tac (LOG_LE_MONO |> qspec_arith `256` |> qspec `n` |> qspec `256 ** j`)
      >> asm_simp_tac arith_ss [LOG_EXACT_RWT])
);


val NWIDTH_MINIMAL_BOUND = Q.prove
(`!n. n < 256 ** NWIDTH n /\ !j. 0 < j /\ n < 256 ** j ==> NWIDTH n <= j`,
 rw_tac arith_ss [NWIDTH_def]
  >- rw_tac arith_ss [LOG |> qspec_arith `256` |> SIMP_RULE bool_ss [ADD1]]
  >- (`LOG 256 n < j` by rw_tac arith_ss [LOG_LESS]
       >> decide_tac)
);

val IWIDTH_def =
 Define 
  `IWIDTH i = 
    let k = NWIDTH (Num (ABS i)) ;
        N = 256 ** k ;
        J = &(N DIV 2) ;
    in
     if -J <= i /\ i < J
       then k
     else k+1`;

(*
g `!i N. (N = (256 ** IWIDTH i)) ==> -(&(N DIV 2)) <= i /\ i < &(N DIV 2)`;
rw_tac int_ss [IWIDTH_def]
 >> rw_tac int_ss []
 >> full_simp_tac int_ss []
*)
	     
val encI_def = 
 Define 
  `encI w i =
     let k = IWIDTH i;
        bytes = MAX w k;
     in if 0 <= i then 
           enc bytes (Num i)
        else 
          let bound = 256 ** bytes;
          in
           enc bytes (bound - Num (ABS i))`
;

val decI_def =
 Define 
   `decI s = 
       let n = dec s;
           bound = 256 ** (STRLEN s);
           half = bound DIV 2;
       in 
        if n < half then 
           int_of_num n
        else -(int_of_num (bound - n))`

(*
g `!i w. decI (encI w i) = i`;
BasicProvers.NORM_TAC int_ss [decI_def, encI_def,MAX_DEF,LET_THM,IWIDTH_def, NWIDTH_def]
 >> full_simp_tac list_ss [dec_enc]
 >- intLib.ARITH_TAC
 >- intLib.ARITH_TAC;

 >- (`~(n < half)` by (UNABBREV_ALL_TAC >> metis_tac[])
     >> 
     >> full_simp_tac int_ss [MAX_DEF]
     >> rw_tac int_ss []
     >> rw_tac int_ss []
     >> full_simp_tac int_ss [dec_enc]
 >- (full_simp_tac bool_ss [qdecide `~(a < b) <=> (b <= a)`]
     `w <= STRLEN (enc w (Num i))` by metis_tac [lower_enc]
 >- (full_simp_tac int_ss [dec_enc]
 >- (full_simp_tac int_ss [dec_enc]
*)

(*
val defn = Hol_defn "width"
  `WIDTH (i:int) (bits:num) = 
    let N = 2 ** (bits - 1) in
      if -(&N) <= i /\ i < &N 
        then bits
      else WIDTH i (bits+1)`;
Defn.tgoal defn;
*)


(* Show that bitwidth i gives smallest k s.t. -(2**(k-1)) <= i < 2**(k-1) *)

val interval_bitwidthN_def =
 Define 
  `interval_bitwidthN lo hi = 
     bitwidthN
       (if 0i <= lo /\ lo <= hi then
           Num hi else 
        if lo < 0i /\ 0i <= hi then
          Num(ABS lo + hi) else
        if lo < 0 /\ hi < 0 then
            Num(ABS lo)
        else ARB)`;

EVAL ``interval_bitwidthN 0 17999``;
EVAL ``interval_bitwidthN 0 127``;
EVAL ``interval_bitwidthN (-128) 127``;

val encI_def = 
 Define 
  `encI w i =
     if Num(ABS i) < 256 ** w then
        enc w (i2n (8*w) i)
     else
        enc w (i2n (bitwidthN (Num(ABS i))) i)`
;

val decI_def =
 Define 
   `decI s = n2i (8 * STRLEN s) (dec s)`
;

val test_def = Define`test w i = (i,decI (encI w i))`;

(*
EVAL ``test 1 0``;
EVAL ``test 1 1``;
EVAL ``test 1 127``;
EVAL ``test 1 128``;


val lemA = 
 LOG_EXP 
 |> qspec `n`
 |> qspec `256`
 |> qspec `1`
 |> SIMP_RULE arith_ss [qdecide `1 < 256`, qdecide `0 < 1`,EVAL ``LOG 256 1``]
;
*)

val lemA = 
 LOG_EXP 
 |> qspec `n`
 |> qspec `b`
 |> qspec `1`
 |> SIMP_RULE arith_ss [qdecide `0 < 1`]
;

EXP_BASE_LT_MONO
 |> qspec `256`
 |> SIMP_RULE bool_ss [qdecide `1 < 256`]
 |> qspec `LOG 256 (256 ** w)`
 |> qspec `LOG 256 n`
 |> SIMP_RULE bool_ss [lemA]
;

val log_lem = Q.prove
(`!b n w. 1 < b /\ 0 < w /\ 0 < n /\ n < b ** w ==> LOG b n < w`,
rw_tac arith_ss []
 >> mp_tac (qspec `b` (GSYM EXP_BASE_LT_MONO))
 >> ASM_REWRITE_TAC []
 >> DISCH_THEN (fn th => SIMP_TAC std_ss [Once th])
 >> `b ** (LOG b n) <= n` by metis_tac [LOG]
 >> decide_tac);

val len_enc = Q.prove
(`!w n. 0 < w /\ n < 2 ** (8*w) ==> (LENGTH (enc w n) = w)`,
rw_tac list_ss [enc_def,layout_def,PAD_RIGHT,EXP_EXP_MULT]
 >> rw_tac arith_ss [numposrepTheory.LENGTH_n2l]
 >> match_mp_tac (qdecide `a <= b ==> ((a:num) + (b - a) = b)`)
 >> rw_tac arith_ss [qdecide `SUC x <= y <=> x < y`,log_lem]);

val deci_enci = Q.store_thm
("deci_enci",
 `!w i. 0 < w /\ -(&(2n ** ((8 * w) - 1))) <= i /\ i < &(2n ** ((8 * w) - 1))
        ==> (deci w (enci w i) = i)`,
 rw_tac int_ss [enci_def, deci_def,dec_enc,n2i_i2n]);

val deci_encis = save_thm
("deci_encis",
 LIST_CONJ
     (map (C qspec_arith deci_enci) [`1`,`2`,`3`,`4`,`5`,`6`,`7`,`8`]));


(*---------------------------------------------------------------------------*)
(* sign+magnitude representation of ints                                     *)
(*---------------------------------------------------------------------------*)

val encZ_def = 
 Define 
  `encZ w i =
     if 0 <= i then 
        #"+" :: enc w (Num (ABS i))
     else #"-" :: enc w (Num (ABS i))`
;

val decZ_def =
 Define 
   `decZ s = 
     case s of 
       | #"+" :: t => int_of_num(dec t)
       | #"-" :: t => -int_of_num(dec t)`
;

Theorem decz_encz : 
  !w i. decZ (encZ w i) = i
Proof
  BasicProvers.NORM_TAC (srw_ss()) [decZ_def, encZ_def,dec_enc] 
  >> intLib.ARITH_TAC
QED
  
val lem = 
  intLib.ARITH_PROVE 
    ``-i < j /\ j < i <=> ((-i < j /\ j < 0) \/ (0 <= j /\ j < i))``;

val encZ_byte_1A = Q.prove
(`!i:int. -256 < i /\ i < 0 ==> ?a. encZ 1 i = [#"-"; a]`,
 BasicProvers.NORM_TAC (srw_ss()) [encZ_def]
 >- intLib.ARITH_TAC
 >- (`Num(ABS i)  < 256` by intLib.ARITH_TAC >> metis_tac [enc_bytes'])
);

val encZ_byte_1B = Q.prove
(`!i:int. 0 <= i /\ i < 256 ==> ?a. encZ 1 i = [#"+"; a]`,
 BasicProvers.NORM_TAC (srw_ss()) [encZ_def]
 >> `Num(ABS i)  < 256` by intLib.ARITH_TAC 
 >> metis_tac [enc_bytes']
);

val encZ_byte_1 = Q.prove
(`!i:int. 
     -256 < i /\ i < 256 
     ==> 
     (?a. encZ 1 i = [#"-";a]) \/ 
     (?a. encZ 1 i = [#"+";a])`,
 metis_tac [encZ_byte_1A, encZ_byte_1B,lem]);

val encZ_byte_2A = Q.prove
(`!i:int. -65536 < i /\ i < 0 ==> ?a b. encZ 2 i = [#"-"; a; b]`,
 BasicProvers.NORM_TAC (srw_ss()) [encZ_def]
 >- intLib.ARITH_TAC
 >- (`Num(ABS i)  < 65536` by intLib.ARITH_TAC >> metis_tac [enc_bytes'])
);

val encZ_byte_2B = Q.prove
(`!i:int. 0 <= i /\ i < 65536 ==> ?a b. encZ 2 i = [#"+"; a; b]`,
 BasicProvers.NORM_TAC (srw_ss()) [encZ_def]
 >> `Num(ABS i)  < 65536` by intLib.ARITH_TAC 
 >> metis_tac [enc_bytes']
);

val encZ_byte_2 = Q.prove
(`!i:int. 
     -65536 < i /\ i < 65536
    ==> 
      (?a b. encZ 2 i = [#"-";a;b]) \/ 
      (?a b. encZ 2 i = [#"+";a;b])`,
 metis_tac [encZ_byte_2A, encZ_byte_2B, lem]);

val encZ_byte_3A = Q.prove
(`!i:int. 
     -16777216 < i /\ i < 0 
    ==> 
     ?a b c. encZ 3 i = [#"-"; a; b; c]`,
 BasicProvers.NORM_TAC (srw_ss()) [encZ_def]
 >- intLib.ARITH_TAC
 >- (`Num(ABS i)  < 16777216` by intLib.ARITH_TAC >> metis_tac [enc_bytes'])
);

val encZ_byte_3B = Q.prove
(`!i:int. 
    0 <= i /\ i < 16777216
   ==> 
   ?a b c. encZ 3 i = [#"+"; a; b; c]`,
 BasicProvers.NORM_TAC (srw_ss()) [encZ_def]
 >> `Num(ABS i)  < 16777216` by intLib.ARITH_TAC 
 >> metis_tac [enc_bytes']
);

val encZ_byte_3 = Q.prove
(`!i:int. 
     -16777216 < i /\ i < 16777216
    ==> 
    (?a b c. encZ 3 i = [#"-";a;b;c]) \/ 
    (?a b c. encZ 3 i = [#"+";a;b;c])`,
 metis_tac [encZ_byte_3A, encZ_byte_3B, lem]);

val encZ_byte_4A = Q.prove
(`!i:int. 
     -4294967296 < i /\ i < 0 
    ==> 
    ?a b c d. encZ 4 i = [#"-"; a; b; c; d]`,
 BasicProvers.NORM_TAC (srw_ss()) [encZ_def]
 >- intLib.ARITH_TAC
 >- (`Num(ABS i)  < 4294967296` by intLib.ARITH_TAC >> metis_tac [enc_bytes'])
);

val encZ_byte_4B = Q.prove
(`!i:int. 
    0 <= i /\ i < 4294967296 
   ==> 
   ?a b c d. encZ 4 i = [#"+"; a; b; c; d]`,
 BasicProvers.NORM_TAC (srw_ss()) [encZ_def]
 >> `Num(ABS i)  < 4294967296` by intLib.ARITH_TAC 
 >> metis_tac [enc_bytes']
);

val encZ_byte_4 = Q.prove
(`!i:int. 
     -4294967296 < i /\ i < 4294967296 
    ==> 
    (?a b c d. encZ 4 i = [#"-";a;b;c;d]) \/ 
    (?a b c d. encZ 4 i = [#"+";a;b;c;d])`,
 metis_tac [encZ_byte_4A, encZ_byte_4B, lem]);

val encZ_byte_8A = Q.prove
(`!i:int. 
    -18446744073709551616 < i /\ i < 0 
    ==> 
    ?a b c d e f g h. 
    encZ 8 i = [#"-"; a; b; c; d; e; f; g; h]`,
 BasicProvers.NORM_TAC (srw_ss()) [encZ_def]
 >- intLib.ARITH_TAC
 >- (`Num(ABS i) < 18446744073709551616` 
       by intLib.ARITH_TAC 
     >> metis_tac [enc_bytes'])
);

val encZ_byte_8B = Q.prove
(`!i:int. 
    0 <= i /\ i < 18446744073709551616 
   ==> 
    ?a b c d e f g h. 
    encZ 8 i = [#"+"; a;b;c;d;e;f;g;h]`,
 BasicProvers.NORM_TAC (srw_ss()) [encZ_def]
 >> `Num(ABS i)  < 18446744073709551616` by intLib.ARITH_TAC 
 >> metis_tac [enc_bytes']
);

val encZ_byte_8 = Q.prove
(`!i:int. 
    -18446744073709551616 < i /\ i < 18446744073709551616 
    ==> 
    (?a b c d e f g h. encZ 8 i = [#"+"; a;b;c;d;e;f;g;h]) \/
    (?a b c d e f g h. encZ 8 i = [#"-"; a;b;c;d;e;f;g;h])`,
 metis_tac [encZ_byte_8A, encZ_byte_8B, lem]);

val encZ_bytes = save_thm
 ("encZ_bytes",
  LIST_CONJ [encZ_byte_1,encZ_byte_2,encZ_byte_3,encZ_byte_4,encZ_byte_8]);


(*---------------------------------------------------------------------------*)
(* Length directed string destructor                                         *)
(*---------------------------------------------------------------------------*)

val split_at_aux_def =
 Define
     `(split_at_aux 0 s acc = SOME(REVERSE acc,s)) /\
      (split_at_aux (SUC n) s acc = 
        case DEST_STRING s
         of NONE => NONE
          | SOME(c,t) => split_at_aux n t (c::acc))`;

val split_at_def =
    Define `split_at n s = split_at_aux n s []`;

val chop_aux_def =
 Define
  `(chop_aux [] s acc = SOME(REVERSE acc,s)) /\
   (chop_aux (n::t) s acc =
     case split_at n s
      of NONE => NONE
       | SOME(pref,suff) => chop_aux t suff (pref::acc))`;

val chop_def =
 Define
  `chop nlist s =
      case chop_aux nlist s []
       of NONE => NONE
        | SOME(lists, suff) => 
          if suff = ""  then SOME lists else NONE`;
						 
val split_at_aux_some = Q.prove
(`!n s acc racc_pref suff. 
    (split_at_aux n s acc = SOME(racc_pref,suff))
    ==> 
    ?pref. (racc_pref = REVERSE acc ++ pref) /\ 
           (s = pref ++ suff) /\ 
           (LENGTH pref = n)`,
 Induct 
  >> rw_tac list_ss [split_at_aux_def]
  >- (EVERY_CASE_TAC
       >- metis_tac[]
       >- (full_simp_tac list_ss [DEST_STRING_LEMS]
	    >> res_tac
            >> rw_tac list_ss []
            >> qexists_tac `q::pref`
            >> rw_tac list_ss []
            >> metis_tac [STRCAT_EQNS,STRCAT_ASSOC])))
;

val strlen_eq_suc = Q.prove
(`!s n. (STRLEN s = SUC n) ==> ?c t. s = STRING c t`,
 Induct >> rw_tac list_ss [STRLEN_THM]);


val split_at_aux_some_eq = Q.prove
(`!n s acc racc_pref suff. 
    (split_at_aux n s acc = SOME(racc_pref,suff))
    <=> 
    ?pref. (racc_pref = REVERSE acc ++ pref) /\ (s = pref ++ suff) /\ (LENGTH pref = n)`,
 Induct
  >> rw_tac list_ss [split_at_aux_def]
  >- metis_tac[]
  >- (EVERY_CASE_TAC
       >> full_simp_tac list_ss [DEST_STRING_LEMS]
       >> rw_tac list_ss [EQ_IMP_THM]
       >- (qexists_tac `q::pref`
           >> rw_tac list_ss []
           >> metis_tac [STRCAT_EQNS,STRCAT_ASSOC])
       >- (`?c t. pref = STRING c t` by metis_tac [strlen_eq_suc]
                 >> rw_tac list_ss []
                 >> full_simp_tac list_ss []
		 >> rw_tac list_ss []
                 >> metis_tac [STRCAT_EQNS,STRCAT_ASSOC])))
;

val split_at_aux_none = Q.prove
(`!n s acc. 
    (split_at_aux n s acc = NONE) ==> LENGTH s < n`,
 Induct 
  >> rw_tac list_ss [split_at_aux_def]
  >> EVERY_CASE_TAC
  >> full_simp_tac list_ss [DEST_STRING_LEMS]
  >> res_tac
  >> rw_tac list_ss [])
;

val split_at_aux_none_eq = Q.prove
(`!n s acc. 
    (split_at_aux n s acc = NONE) <=> LENGTH s < n`,
 Induct 
  >> rw_tac list_ss [split_at_aux_def]
  >> EVERY_CASE_TAC
  >> full_simp_tac list_ss [DEST_STRING_LEMS]);
;

val split_at_some = Q.prove
(`!n s pref suff.
    (split_at n s = SOME (pref,suff))
    ==> 
    (s = pref ++ suff) /\ (LENGTH pref = n)`,
 rw_tac list_ss [split_at_def]
  >> imp_res_tac split_at_aux_some
  >> rw_tac list_ss []
  >> full_simp_tac list_ss []);

val split_at_some_eq = Q.prove
(`!n s pref suff.
    (split_at n s = SOME (pref,suff))
    <=> 
    (s = pref ++ suff) /\ (LENGTH pref = n)`,
 rw_tac list_ss [split_at_def,split_at_aux_some_eq]);

val split_at_none = Q.prove
(`!n s acc. 
    (split_at n s = NONE) ==> LENGTH s < n`,
metis_tac [split_at_def, split_at_aux_none])

val split_at_none_eq = Q.prove
(`!n s acc. 
    (split_at n s = NONE) <=> LENGTH s < n`,
 metis_tac [split_at_def, split_at_aux_none_eq]);

  
val chop_aux_lem = Q.prove
(`!nlist s acc racc_lists suff.
     (chop_aux nlist s acc = SOME (racc_lists,suff))
     ==> 
     ?lists. (racc_lists = REVERSE acc ++ lists) /\
            (LENGTH racc_lists = LENGTH nlist + LENGTH acc) /\
            LIST_REL (\n list. n = LENGTH list) nlist lists /\
            (s = FLAT lists ++ suff)`,
 Induct
 >- (rw_tac list_ss [chop_aux_def] >> metis_tac [LENGTH_REVERSE])
 >- (rw_tac list_ss [chop_aux_def]
     >> EVERY_CASE_TAC
     >- metis_tac []
     >- (full_simp_tac list_ss [split_at_some_eq]
          >> rw_tac list_ss []
          >> res_tac
          >> rw_tac list_ss []
          >> qexists_tac `[q] ++ lists`
          >> rw_tac list_ss [])))
;

val chop_thm = Q.store_thm
("chop_thm",
 `!nlist s lists. 
    (chop nlist s = SOME lists)
    ==> 
    (LENGTH lists = LENGTH nlist) /\
    LIST_REL (\n list. n = LENGTH list) nlist lists /\
    (s = FLAT lists)`,
 rw_tac list_ss [chop_def]
 >> EVERY_CASE_TAC
 >> rw_tac list_ss []
 >> imp_res_tac chop_aux_lem
 >> full_simp_tac list_ss []);


(*---------------------------------------------------------------------------*)
(* Enumerations need bijections between constructors and numbers. Booleans   *)
(* are built in, others are defined when the AADL package is processed.      *)
(*---------------------------------------------------------------------------*)

val num_of_bool_def =
    Define `(num_of_bool F = 0n) /\ (num_of_bool T = 1n)`;

val bool_of_num_def =
    Define `bool_of_num n = if n = 0n then F else T`

val enc_bool_def =
    Define `enc_bool b = enc 1 (num_of_bool b)`;

val dec_bool_def =
    Define `dec_bool s = bool_of_num(dec s)`;

val dec_enc_bool = Q.store_thm
("dec_enc_bool",
 `!b. dec_bool (enc_bool b) = b`,
 Cases 
 >> rw_tac std_ss 
      [enc_bool_def, dec_bool_def,dec_enc,num_of_bool_def,bool_of_num_def])

val bool_bound = Q.store_thm
("bool_bound",
 `!b. num_of_bool b < 256`,
 Cases >> rw_tac arith_ss [num_of_bool_def]);

val num_of_bool_bound = Q.store_thm
("num_of_bool_bound",
 `!b. num_of_bool b < 2`,
 Cases >> rw_tac arith_ss [num_of_bool_def]);

val FILTER_CORRECT_def = 
 Define 
   `FILTER_CORRECT s (tm:bool) = tm`;

val fcp_every_thm = save_thm
("fcp_every_thm",
 fcpTheory.FCP_EVERY_def 
   |> SIMP_RULE (srw_ss()) 
        [DECIDE ``!m n:num. m <= n <=> ~(n < m)``, Once (GSYM IMP_DISJ_THM)])

val _ = export_theory();
