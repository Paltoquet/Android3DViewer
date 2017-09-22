package test.opengl.Shapes;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import test.opengl.ObjLoader.Build;
import test.opengl.ObjLoader.Parse;
import test.opengl.R;
import test.opengl.Utils.Config;

/**
 * Created by user on 21/09/2017.
 */
public class Mesh {

    static final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * Config.BYTE_PER_FLOAT; // 4 bytes per vertex


    private float[] m_vertices;
    private short[] m_indices;

    private float[] m_test = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f,  0.5f, 0.0f };

    private short[] m_test2 = {
            0, 1, 2, 0, 2, 3
    };

    private FloatBuffer m_vertexBuffer;
    private ShortBuffer m_indexBuffer;

    private int[] m_vbo = new int[1];
    private int[] m_ibo = new int[1];
    private int m_MVPHandle;
    private int m_PositionHandle;
    private int m_ColorHandle;

    private int floatCount;
    private int indexCount;

    private Parse m_parse;
    private Build m_build;
    private Context m_context;
    private int m_ressourceId;
    // Set color with red, green, blue and alpha (opacity) values
    private float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Mesh(Context context, int ressourcesId) {

        m_context = context;
        m_ressourceId = ressourcesId;
        m_build = new Build();
        try {
            m_parse = new Parse(context, m_build, ressourcesId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        m_vertices = m_build.getVertices();
        m_indices = m_build.getIndices();
        floatCount = m_vertices.length;//m_build.verticesG.size() * COORDS_PER_VERTEX;
        indexCount = m_indices.length;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                floatCount * Config.BYTE_PER_FLOAT);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        m_vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        m_vertexBuffer.put(m_vertices);
        // set the buffer to read the first coordinate
        m_vertexBuffer.position(0);

        ByteBuffer indexBuff = ByteBuffer.allocateDirect(indexCount * Config.BYTE_PER_SHORT);
        indexBuff.order(ByteOrder.nativeOrder());

        m_indexBuffer = indexBuff.asShortBuffer();
        m_indexBuffer.put(m_indices);
        m_indexBuffer.position(0);

        GLES20.glGenBuffers(1, m_vbo, 0);
        GLES20.glGenBuffers(1, m_ibo, 0);

        //set vertex Buffer
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, m_vertexBuffer.capacity()
                * Config.BYTE_PER_FLOAT, m_vertexBuffer, GLES20.GL_STATIC_DRAW);

        //set index buffer
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, m_ibo[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, m_indexBuffer.capacity()
                * Config.BYTE_PER_SHORT, m_indexBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void draw(int program, float[] mvp) {
        // get handle to vertex shader's vPosition member

        Log.d("mesh", "test " + String.valueOf(floatCount) + " short: " + String.valueOf(m_indices.length));

        m_MVPHandle = GLES20.glGetUniformLocation(program, "MVPMatrix");
        m_PositionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        m_ColorHandle = GLES20.glGetUniformLocation(program, "vColor");

        GLES20.glUniformMatrix4fv(m_MVPHandle, 1, false, mvp, 0);
        GLES20.glUniform4fv(m_ColorHandle, 1, color, 0);

        GLES20.glEnableVertexAttribArray(m_PositionHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_vbo[0]);
        // Enable a handle to the triangle vertices
        GLES20.glVertexAttribPointer(m_PositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, vertexStride, 0);

        /*// Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        */
        // get handle to fragment shader's! vColor member

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, m_ibo[0]);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);

        // Draw the triangle
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        //GLES20.glDisableVertexAttribArray(m_PositionHandle);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    }
}
