/* Objective function */
min: +5 C4 +5 C5 +C7 +C8;

/* Constraints */
+C1 -C2 +38.6 C4 <= 31.1;
R2: +C1 <= 0;
R3: +C2 <= 12;
R4: +C1 >= 0;
R5: +C2 >= 0;
R6: +C4 = 1;
R7: +C5 = 1;
+C3 +C5 = 1;
+C4 +C6 = 1;
+C1 +C7 = 0;
+C2 +C8 = 11;

/* Variable bounds */
C3 <= 1;
C4 <= 1;
C5 <= 1;
C6 <= 1;

/* Integer definitions */
int C3,C4,C5,C6;
