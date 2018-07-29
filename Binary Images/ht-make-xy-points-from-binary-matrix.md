Image preparation: Open in GIMP, Select Image --> Mode --> Indexed (1-bit image).

A = [];
A = imread("foo.png");
// input matrix of m x n dimensions.

A = bool2s(A);
// changes A from a Boolean matrix into a Double.

B = matrix(A,25,1);
// changes matrix to single column. Indexed column-wise. 25 is a placeholder (actual number is m x n).

b = find (B==1);
b = b';
//all ones in binary matrix are in single column. Need to convert string to 
double for B? Or is B==255?

x = size(A);
y = x(:,1);
// find column length of matrix A (the m in m x n).

yy = 1+(b-1/y);
yy = floor(yy);
// find column number. New x variable for x,y coordinate.

zz = b/y;
zz = zz - floor(zz);
zz = zz * y;
// find row number. New y variable for x,y coordinate.

plot(yy,zz,'o');

x = [x1;x2;x3];
// horizontal concantenation of three matrices.
//locations of all ones in binary matrix.
// for zz, replace all "0" values with y (in this case, "5"). Should equate to all values in the bottom row.
