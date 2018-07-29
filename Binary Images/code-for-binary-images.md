atomsInstall("IPCV");
atomsInstall("stixbox");

A = imread("A.png");
a = dec2bin(A);
save("A.txt","a");
//binary matrix of size m x n.

aa = strtod(a);
imshow(a);
//plots one bit representation of matrix.
