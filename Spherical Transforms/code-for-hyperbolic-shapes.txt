C. elegans embryo volume data:

x = A(:,1);
y = A(:,2);
v = 2*(x.*y);
scatter3(v,x,y,"red","fill","o");
// saddle-shaped space.

scatter3(u,x,y,"red","fill","o");
// u-shaped bend around x=0. Concave curvature about z-axis.

u = sqrt(x^2+y^2+z^2);
scatter3(u,x,y,"red","fill","o");
// u-shaped bend around x=0. Convex curvature about z-axis.

x = A(:,1);
y = A(:,2);
z = A(:,3);
u = %pi*(sqrt(x^2+y^2));
// v-shaped stripes from origin (along z-axis).

x = A(:,1);
y = A(:,2);
u = %pi*(x^2.*y^2);
v = 2*(x.*y);
z = A(:,3);
// circular band around z-axis. Curvature toward bottom.


Drosophila digital imaginal disc:

1) U-transform.
u = x^2 - y^2;
scatter3(u,x,y,"red","fill","o");
// plots a copy of x,y map along z-axis.

2) circular transform.
u = %pi*(x^2-y^2);
v = 2xy;
plot(u,v);
// curved shell surface.

3) circular distance transform.
m = sqrt(x^2+y^2);
u = %pi*(m);
v = 2xy;
plot(u,v);
// more raked version of (2).
