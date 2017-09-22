package test.opengl;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import test.opengl.Shaders.Shader;
import test.opengl.Shapes.Mesh;
import test.opengl.Shapes.SimpleMesh;
import test.opengl.Shapes.Square;
import test.opengl.Shapes.Triangle;

/**
 * Created by user on 17/09/2017.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private int m_program = 0;


    private Mesh m_mesh;
    private Triangle m_triangle;
    private Square m_square;
    private Context m_context;
    public volatile float m_Angle;



    private float[] m_RotationMatrix = new float[16];
    private float[] m_MVPMatrix = new float[16];
    private float[] m_ProjectionMatrix = new float[16];
    private float[] m_ViewMatrix = new float[16];


    public MyGLRenderer(Context context){
        m_context = context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        m_triangle = new Triangle();
        m_square = new Square();
        m_mesh = new Mesh(m_context, R.raw.puss);
        // setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX,
        // float centerY, float centerZ, float upX, float upY, float upZ)
        Matrix.setLookAtM(m_ViewMatrix, 0, 0, -2, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        m_program = GLES20.glCreateProgram();
        int vertexShader = Shader.loadShader(GLES20.GL_VERTEX_SHADER,
                Shader.vertexShaderCode);
        int fragmentShader = Shader.loadShader(GLES20.GL_FRAGMENT_SHADER,
                Shader.fragmentShaderCode);

        // add the vertex shader to program
        GLES20.glAttachShader(m_program, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(m_program, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(m_program);

    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        // Calculate the projection and view transformation
        Matrix.setRotateM(m_RotationMatrix, 0, m_Angle, 0, -1.0f, 0f);
        Matrix.multiplyMM(m_MVPMatrix, 0, m_ViewMatrix, 0, m_RotationMatrix, 0);
        Matrix.multiplyMM(m_MVPMatrix, 0, m_ProjectionMatrix, 0, m_MVPMatrix, 0);
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glUseProgram(m_program);
        m_mesh.draw(m_program, m_MVPMatrix);
        //m_triangle.draw(m_program, m_MVPMatrix);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        //frustumM(float[] m, int offset, float left, float right, float bottom, float top, float near, float far)
        Matrix.frustumM(m_ProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public float getAngle() {
        return m_Angle;
    }

    public void setAngle(float angle) {
        m_Angle = angle;
    }
}