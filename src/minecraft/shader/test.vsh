uniform float timeMs;
uniform vec2 resolution;

void main(){
    gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;
}