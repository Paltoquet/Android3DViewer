package test.opengl;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import test.opengl.Math.Matrix4;
import test.opengl.Math.Quaternion;
import test.opengl.Shaders.Shader;
import test.opengl.Shapes.Mesh;
import test.opengl.Shapes.MeshPack;
import test.opengl.Shapes.SimpleMesh;
import test.opengl.Shapes.Square;
import test.opengl.Shapes.TextureMesh;
import test.opengl.Shapes.Triangle;

/**
 * Created by user on 17/09/2017.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private int m_program = 0;


    private MeshPack m_meshPacked;
    private Mesh m_mesh;
    private Triangle m_triangle;
    private Square m_square;
    private Context m_context;
    public volatile float m_Angle;
    public volatile Quaternion m_Rotation;



    private volatile float[] m_RotationMatrix = new float[16];
    private float[] m_MVPMatrix = new float[16];
    private float[] m_ProjectionMatrix = new float[16];
    private float[] m_ViewMatrix = new float[16];


    public MyGLRenderer(Context context){
        m_context = context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color

        //GLES20.glCullFace(GLES20.GL_BACK);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        m_triangle = new Triangle();
        m_square = new Square();
        //m_mesh = new Mesh(m_context, R.raw.puss);
        m_meshPacked = new MeshPack(m_context, R.raw.puss, R.drawable.text);

        m_Rotation = new Quaternion(1,0,0,0);
        m_RotationMatrix = Matrix4.identity().toArray();

        // setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX,
        // float centerY, float centerZ, float upX, float upY, float upZ)
        Matrix.setLookAtM(m_ViewMatrix, 0, 0, 0, -4, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        m_program = GLES20.glCreateProgram();
        int vertexShader = Shader.loadShader(GLES20.GL_VERTEX_SHADER,
                Shader.textureVertexShader);
        int fragmentShader = Shader.loadShader(GLES20.GL_FRAGMENT_SHADER,
                Shader.textureFragmentShader);

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
        //Matrix.setRotateM(m_RotationMatrix, 0, m_Angle, 0, -1.0f, 0f);

        Matrix.multiplyMM(m_MVPMatrix, 0, m_ViewMatrix, 0, m_RotationMatrix, 0);
        Matrix.multiplyMM(m_MVPMatrix, 0, m_ProjectionMatrix, 0, m_MVPMatrix, 0);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glUseProgram(m_program);
        //m_mesh.draw(m_program, m_MVPMatrix);
        m_meshPacked.draw(m_program, m_MVPMatrix);
        //m_textureMesh.draw(m_program, m_MVPMatrix);
        //m_triangle.draw(m_program, m_MVPMatrix);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        //frustumM(float[] m, int offset, float left, float right, float bottom, float top, float near, float far)
        Matrix.frustumM(m_ProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 30);
    }

    public float[] getRotationMatrix(){
        return m_RotationMatrix;
    }

    public float[] getViewMatrix(){
        return m_ViewMatrix;
    }

    public float getAngle() {
        return m_Angle;
    }

    public void setAngle(float angle) {
        m_Angle = angle;
    }

    public void rotate(Quaternion quat){
        //m_Rotation = m_Rotation.mul(quat.toMatrix());
        float[] res = new float[16];
        Log.d("before", String.valueOf(m_Rotation) + "quat: " + String.valueOf(quat));
        quat.normalize();
        //m_Rotation = m_Rotation.mul(quat);
        //m_Rotation.normalize();
        Log.d("after", String.valueOf(m_Rotation));
        //m_RotationMatrix = m_Rotation.toMatrix().getAsArray();
        Matrix.multiplyMM(res,0,m_RotationMatrix, 0, quat.toMatrix().toArray(), 0);
        //m_RotationMatrix = m_Rotation.toMatrix().getAsArray();
        m_RotationMatrix = res;
        Log.d("rot", res[0] + "," + res[1] + "," + res[2] + "," + res[3]);
        Log.d("rot", res[4] + "," + res[5] + "," + res[6] + "," + res[7]);
        Log.d("rot", res[8] + "," + res[9] + "," + res[10] + "," + res[11]);
        Log.d("rot", res[12] + "," + res[13] + "," + res[14] + "," + res[15]);

    }

    public void rotate(float[] rot){
        Matrix.multiplyMM(m_RotationMatrix, 0, m_RotationMatrix, 0, rot, 0);
    }
}