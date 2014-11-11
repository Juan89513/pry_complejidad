/* Objective function */
min: +5 C5 +7 C6 +13 C9;

/* Constraints */
+C1 -C2 +193.7 C4 <= 186.2;
+C1 -C3 +193.7 C5 <= 184.2;
-C1 +C2 +193.7 C6 <= 192.6;
+C2 -C3 +193.7 C7 <= 179.6;
-C1 +C3 +193.7 C8 <= 190.6;
-C2 +C3 +193.7 C9 <= 190.6;
R7: +C1 <= 20;
R8: +C2 <= 32;
R9: +C3 <= 50;
R10: +C1 >= 0;
R11: +C2 >= 0;
R12: +C3 >= 0;
+C5 +C6 = 1;
R14: +C9 = 1;

/* Variable bounds */
C4 <= 1;
C5 <= 1;
C6 <= 1;
C7 <= 1;
C8 <= 1;
C9 <= 1;
C10 <= 1;
C11 <= 1;

/* Integer definitions */
int C4,C5,C6,C7,C8,C9,C10,C11;
