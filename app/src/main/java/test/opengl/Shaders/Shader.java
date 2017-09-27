package test.opengl.Shaders;

import android.opengl.GLES20;

/**
 * Created by user on 17/09/2017.
 */
public class Shader {


    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    public static final String vertexShaderCode =
            "attribute vec3 vPosition;" +
            "uniform mat4 MVPMatrix;" +
            "void main() {" +
            "  gl_Position = MVPMatrix * vec4(vPosition,1);" +
            "}";

    public static final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    public  static  final String textureVertexShader =
            "attribute vec3 vPosition;" +
            "attribute vec2 vTexCoordinate;" +
            "uniform mat4 MVPMatrix;" +
            "uniform vec4 vColor;" +
            "varying vec2 fTexCoordinate;" +
            "void main() {" +
            "  gl_Position = MVPMatrix * vec4(vPosition,1);" +
            "  fTexCoordinate = vTexCoordinate;" +
            "}";

    public static final String textureFragmentShader =
            "precision mediump float;" +
            "uniform sampler2D fTexture;" +
            "varying vec2 fTexCoordinate;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = texture2D(fTexture, fTexCoordinate);" +
            "}";
}
