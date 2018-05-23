#include <stdio.h>


int main()
{
	int A;
	int B;
	int I;
	A = 0;
	B = 1 * 5;
	scanf("%d", &A);
	printf("%d %d", A, B);
	for(I = 0; I < 3; I = I + 1)
		A = A + 1;
	if(A > B)
		A = B;
	return 0;

}
