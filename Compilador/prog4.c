#include <stdio.h>


float retorno;
int Mod(int x, int y)
{
	int resto;
	resto = x - (y * (x / y));
	return resto;

}
void PrimeNumbers()
{
	char espaco[] = " ";
	int i, j, primo, auxi, auxj, mod;
	for(i = 1; i < 100; i = i + 1)
	{
		primo = 0;
		auxi = i + 1;
		for(j = 0; j < auxi; j = j + 1)
		{
			auxj = j + 1;
			mod = Mod(auxi, auxj);
			if(mod = 0)
				primo = primo + 1;
		}
		if(primo < 2)
		{
			printf("%d", i);
			printf("%s", espaco);
		}
		if(primo = 2)
		{
			printf("%d", i);
			printf("%s", espaco);
		}
	}

}
int main()
{
	PrimeNumbers();
	return 0;

}
