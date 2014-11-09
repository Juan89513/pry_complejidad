/* Objective function */
min: +C1 +C2;

/* Constraints */
+C1 -C2 +5000 C3 <= 4992.5;
-C1 +C2 +5000 C4 <= 4991.9;
R3: +C1 <= 5;
R4: +C2 <= 12;
R5: +C1 >= 2;
R6: +C2 >= 10;
R7: +C1 >= 0;
R8: +C2 >= 0;
