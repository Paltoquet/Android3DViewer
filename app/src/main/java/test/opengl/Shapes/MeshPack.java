package test.opengl.Shapes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import test.opengl.ObjLoader.Build;
import test.opengl.ObjLoader.Parse;
import test.opengl.Utils.Config;
import test.opengl.Utils.VBO;

/**
 * Created by user on 23/09/2017.
 */
public class MeshPack{

    static int COORDS_PER_VERTEX = VBO.STRIDE ;
    private int vertexStride = COORDS_PER_VERTEX * Config.BYTE_PER_FLOAT; // 4 bytes per vertex

    protected FloatBuffer m_vertexBuffer;
    protected ShortBuffer m_indexBuffer;

    protected int[] m_vbo = new int[1];
    protected int[] m_ibo = new int[1];

    protected VBO m_vboData;
    protected float[] m_vertices;
    protected short[] m_indices;
    protected float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    protected int m_MVPHandle;
    protected int m_PositionHandle;
    protected int m_textureCoordinateHandle;
    protected int m_ColorHandle;
    protected int   m_textureUniformHandle;
    protected int[] m_textureHandle  = new int[1];

    protected int floatCount;
    protected int indexCount;

    protected Parse m_parse;
    protected Build m_build;
    protected Context m_context;
    protected int m_ressourceId;
    protected int m_ressourcesTextureId;

    public MeshPack(Context context, int meshID, int textureID) {

        m_context = context;
        m_ressourceId = meshID;
        m_ressourcesTextureId = textureID;
        m_build = new Build();
        try {
            m_parse = new Parse(context, m_build, meshID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        m_vboData = new VBO(m_build.faceVerticeList);
        m_indices = m_build.getIndices();
        m_vertices = m_vboData.vbo;
        createBuffer();
        createTexture();
    }

    public void draw(int program, float[] mvp) {
        // get handle to vertex shader's vPosition member
        Log.d("mesh test", "test " + String.valueOf(floatCount) + " short: " + String.valueOf(m_indices.length));

        getHandle(program);
        setUniform(mvp);
        setBuffer();
        drawElem();
        exit();
    }

    protected void createBuffer(){
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

    protected void createTexture(){

        GLES20.glGenTextures(1, m_textureHandle, 0);

        if (m_textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(m_context.getResources(), m_ressourcesTextureId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (m_textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }
    }
    protected void getHandle(int program){
        m_MVPHandle = GLES20.glGetUniformLocation(program, "MVPMatrix");
        m_textureUniformHandle = GLES20.glGetUniformLocation(program, "fTexture");
        m_PositionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        m_textureCoordinateHandle = GLES20.glGetAttribLocation(program, "vTexCoordinate");
        m_ColorHandle = GLES20.glGetUniformLocation(program, "vColor");
    }

    protected void setUniform(float[] mvp){
        GLES20.glUniformMatrix4fv(m_MVPHandle, 1, false, mvp, 0);
        GLES20.glUniform4fv(m_ColorHandle, 1, color, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_textureHandle[0]);
        GLES20.glUniform1i(m_textureUniformHandle, 0);
    }

    protected void setBuffer(){
        GLES20.glEnableVertexAttribArray(m_PositionHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_vbo[0]);
        // Enable a handle to the triangle vertices
        GLES20.glVertexAttribPointer(m_PositionHandle, 3, GLES20.GL_FLOAT,
                false, vertexStride, 0);

        GLES20.glEnableVertexAttribArray(m_textureCoordinateHandle);
        // Enable a handle to the triangle vertices
        GLES20.glVertexAttribPointer(m_textureCoordinateHandle, 2, GLES20.GL_FLOAT,
                false, vertexStride, 3*Config.BYTE_PER_FLOAT);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, m_ibo[0]);
    }

    protected void drawElem(){
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);
    }

    protected void exit(){
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, m_ibo[0]);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_vbo[0]);
    }
}
