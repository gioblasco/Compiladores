#include <stdio.h>;


float retorno;
void MAIN()
{
	retorno = PrimeNumbers();

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
		//printf("%t", i);
		//printf("%t", espaco);
	}
	if(primo = 2)
	{
		//printf("%t", i);
		//printf("%t", espaco);
	}
}

}
int Mod(int x, int y)
{
	int resto;
	resto = x - (y * (x / y));
	return resto;

}
