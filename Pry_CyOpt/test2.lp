/* Objective function */
min: +5 C4 +5 C5 +C11 +C12;

/* Constraints */
+C1 -C2 +44.6 C4 <= 37.1;
-C1 +C2 +44.6 C5 <= 36.5;
R3: +C1 <= 5;
R4: +C2 <= 12;
R5: +C1 >= 0;
R6: +C2 >= 0;
R7: +C8 = 1;
R8: +C9 = 1;
+C3 +C9 +C11 = 1;
R10: +C10 = 1;
+C1 +C7 = 2;
+C2 +C8 = 10;

/* Variable bounds */
C3 <= 1;
C4 <= 1;
C5 <= 1;
C6 <= 1;
C7 <= 1;
C8 <= 1;
C9 <= 1;
C10 <= 1;

/* Integer definitions */
int C3,C4,C5,C6,C7,C8,C9,C10;
