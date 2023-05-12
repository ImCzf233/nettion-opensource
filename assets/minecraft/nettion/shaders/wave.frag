#version 120

uniform float time;
uniform vec2 resolution;

float field(in vec3 p,float s) {
	float strength = 7. + .03 * log(1.e-6 + fract(sin(time) * 4373.11));
	float accum = s/4.;
	float prev = 0.;
	float tw = 0.;
	for (int i = 0; i < 26; ++i) {
		float mag = dot(p, p);
		p = abs(p) / mag + vec3(-.5, -.4, -1.5);
		float w = exp(-float(i) / 7.);
		accum += w * exp(-strength * pow(abs(mag - prev), 2.2));
		tw += w;
		prev = mag;
	}
	return max(0., 5. * accum / tw - .7);
}

float field2(in vec3 p, float s) {
	float strength = 7. + .03 * log(1.e-6 + fract(sin(time) * 4373.11));
	float accum = s/4.;
	float prev = 0.;
	float tw = 0.;
	for (int i = 0; i < 18; ++i) {
		float mag = dot(p, p);
		p = abs(p) / mag + vec3(-.5, -.4, -1.5);
		float w = exp(-float(i) / 7.);
		accum += w * exp(-strength * pow(abs(mag - prev), 2.2));
		tw += w;
		prev = mag;
	}
	return max(0., 5. * accum / tw - .7);
}

vec3 nrand3( vec2 co ) {
	vec3 a = fract( cos( co.x*8.3e-3 + co.y )*vec3(1.3e5, 4.7e5, 2.9e5) );
	vec3 b = fract( sin( co.x*0.3e-3 + co.y )*vec3(.1e5, 1.0e5, 0.1e5) );
	vec3 c = mix(a, b, 0.5);
	return c;
}


void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
	vec2 uv = 2. * fragCoord.xy / resolution.xy - 1.;
	vec2 uvs = uv * resolution.xy / max(resolution.x, resolution.y);
	vec3 p = vec3(uvs / 4., 0) + vec3(1., -1.3, 0.);
	p += .2 * vec3(sin(time / 16.), sin(time / 12.), sin(time / 128.));

	float freqs[4];
	// TODO: ���Ӷ�Һ���˻ص�����֧��
	// https://github.com/CCBlueX/LiquidBounce-Issues/issues/3932
	freqs[0] = 0.02;
	freqs[1] = 0.07;
	freqs[2] = 0.15;
	freqs[3] = 0.30;

	float t = field(p,freqs[2]);
	float v = (1. - exp((abs(uv.x) - 1.) * 6.)) * (1. - exp((abs(uv.y) - 1.) * 6.));

	//�ڶ���
	vec3 p2 = vec3(uvs / (4.+sin(time *0.11)*0.2+0.2+sin(time *0.15)*0.3+1.1), 1.5) + vec3(1.6, 1.3, -1.4);
	p2 += .25 * vec3(sin(time / 16.), sin(time / 5.), sin(time / 128.));
	float t2 = field2(p2,freqs[3]);
	vec4 c2 = mix(1.4, 0.5, v) * vec4(3.8 * t2 * t2 * t2 , 3.5 * t2 * t2 , 2.5 * t2, t2);


	//����������һЩ����
	//vec2 seed = p.xy * 2.0;
	//seed = floor(seed * resolution.x);
	//vec3 rnd = nrand3( seed );
	//vec4 starcolor = vec4(pow(rnd.y,10.0));

	//�ڶ���
	vec2 seed2 = p2.xy * 2.0;
	seed2 = floor(seed2 * resolution.x);
	vec3 rnd2 = nrand3( seed2 );
	vec4 starcolor = vec4(pow(rnd2.y, 10000000000000000000000000.0));

	fragColor = mix(freqs[3]-.3, 1., v) * vec4(1.5*freqs[2] * t * t* t , 1.2*freqs[1] * t * t, freqs[3]*t, 1.0)+c2+starcolor;
}

void main() {
	mainImage(gl_FragColor, gl_FragCoord.xy);
}