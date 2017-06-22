#version 330

in vec3 vNormal;
in vec3 vViewPath;
in vec3 aFragPos;
out vec4 outColor;

//Propriedades da luz
uniform vec3 uAmbientLight; //cor da luz ambiente
uniform vec3 uDiffuseLight; //cor da luz difusa
uniform vec3 uSpecularLight;    //cor da luz especular

//Propriedades da luz direcional
uniform vec3 uLightDir; //direção da luz difusa


//Propriedades do material
uniform vec3 uAmbientMaterial;  //cor do ambientMaterial
uniform vec3 uDiffuseMaterial;  //direção do diffuseMaterial
uniform vec3 uSpecularMaterial;     //cor da luz especular
uniform float uSpecularPower;   //intensidade do material quanto a luz especular


void main() {
    vec3 N = normalize(vNormal);
    vec3 L = normalize(vViewPath);

    //Calculo do componente ambiente
    vec3 ambient = uAmbientLight + uAmbientMaterial;

    //Calcula o componente difuso
    float intensity = dot(N, -L); // cosseno entre os vetores normalizados N e L
    vec3 diffuse = clamp(uDiffuseLight * intensity, 0.0, 1.0) * uDiffuseMaterial;

    //Calcula o componente Especular
    float specularIntensity = 0.0f;
    if(uSpecularPower > 0.0){
        vec3 V = normalize(vViewPath);
        vec3 R = reflect(L, N);
        specularIntensity = pow(max(dot(V, R), 0.0), uSpecularPower);
    }
    vec3 specular = specularIntensity * uSpecularLight * uSpecularMaterial;

    //Combina os componentes
    vec3 color = clamp(ambient + diffuse + specular, 0.0, 1.0);
    outColor = vec4(color, 1.0f);
}