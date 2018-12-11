For FFT of data:   

zero-pad (or mean-pad) data by 50 points (974+50 = N).  

sample_rate = 1;  
N = 1024; // number of samples.  
s = a; // input data.  
y = fft(s);  
f = sample_rate*(0:(N/2))/N;  
n = size(f,'*');  //*
clf();  
plot(f,abs(y(1:n)));  
