#include <stdio.h>


float x, y, retorno;
float AbsSub(float num1, float num2)
{
	float num3;
	num3 = num1 - num2;
	if(num3 < 0.0)
		num3 = num3 * (0.0 - 1.0);
	return num3;

}
int Factorial(int n)
{
	if(n = 1)
		return 1;
	return n * (Factorial(n - 1));

}
int main()
{
	int aux;
	scanf("%f", &x);
	scanf("%f", &y);
	retorno = AbsSub(x, y);
	printf("%f", retorno);
	aux = Factorial(10);
	printf("%d", aux);
	return 0;

}
