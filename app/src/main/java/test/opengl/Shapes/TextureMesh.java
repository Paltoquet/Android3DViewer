package test.opengl.Shapes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import test.opengl.Utils.Config;
import test.opengl.Utils.VBO;

/**
 * Created by user on 22/09/2017.
 */
public class TextureMesh extends Mesh {


    protected int   m_textureUniformHandle;
    protected int[] m_textureHandle  = new int[1];

    protected float[] m_textureCoordinates;
    protected FloatBuffer m_textureCoordinatesBuffer;
    protected int[] m_to = new int[1];
    protected int m_textureCoordinatesHandle;

    public TextureMesh(Context context, int meshID, int textureID) {
        super(context, meshID);
        GLES20.glGenTextures(1, m_textureHandle, 0);

        //m_vboData = new VBO(m_build.faces);
        m_textureCoordinates = m_build.getTextureCoordinates();

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                m_textureCoordinates.length * Config.BYTE_PER_FLOAT);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        // create a floating point buffer from the ByteBuffer
        m_textureCoordinatesBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        m_textureCoordinatesBuffer.put(m_textureCoordinates);
        // set the buffer to read the first coordinate
        m_textureCoordinatesBuffer.position(0);
        GLES20.glGenBuffers(1, m_to, 0);
        //set vertex Buffer
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_to[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, m_textureCoordinatesBuffer.capacity()
                * Config.BYTE_PER_FLOAT, m_textureCoordinatesBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, m_to[0]);

        if (m_textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), textureID, options);

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
        super.getHandle(program);
        m_textureUniformHandle        = GLES20.glGetUniformLocation(program, "fTexture");
        m_textureCoordinatesHandle    = GLES20.glGetAttribLocation(program, "vTexCoordinate");
    }

    protected void setUniform(float[] mvp){
        super.setUniform(mvp);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_textureHandle[0]);
        GLES20.glUniform1i(m_textureUniformHandle, 0);
    }

    protected void setBuffer(){
        Log.d("texture", String.valueOf(m_textureCoordinates.length));
        super.setBuffer();
        GLES20.glEnableVertexAttribArray(m_textureCoordinatesHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_to[0]);
        GLES20.glVertexAttribPointer(m_textureCoordinatesHandle, 2, GLES20.GL_FLOAT,
                false, 2 * Config.BYTE_PER_FLOAT, 0);
    }

    protected void exit(){
        super.exit();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_to[0]);
    }

}
