#include <stdio.h>


int x, y, z;
int main()
{
	scanf("%d", &x);
	scanf("%d", &y);
	z = x - y;
	if(z < 0)
		z = z * (0 - 1);
	printf("%d", z);
	return 0;

}
