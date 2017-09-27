package test.opengl.Actors;

import android.content.Context;
import android.opengl.Matrix;

import test.opengl.Math.Quaternion;
import test.opengl.Math.Vector3;
import test.opengl.Shapes.MeshPack;

/**
 * Created by user on 27/09/2017.
 */
public class Ring extends MeshPack{


    protected float[] m_RotationMatrix;
    protected Quaternion m_init;
    protected Quaternion m_end;
    protected boolean m_isRunning;
    protected float m_animationDuration;
    protected float m_progress;

    public Ring(Context context, int meshID, int textureID) {
        super(context, meshID, textureID);
        m_RotationMatrix = new float[16];
        Matrix.setIdentityM(m_RotationMatrix, 0);
        m_init = new Quaternion(1,0,0,0);
        m_end = new Quaternion(new Vector3(0,1,0), 1.5707963268f);
        m_isRunning = false;
        m_animationDuration = 2000;
        m_progress = 0;
    }

    protected void animate(float delta){
        m_progress += delta;
        float ratio = m_animationDuration/m_progress;
        Quaternion tmp = new Quaternion(1.0f,0,0,0);
        tmp.slerp(m_end, ratio);
        m_RotationMatrix = tmp.toMatrix().getAsArray();
    }

    protected void setUniform(float[] mvp){

    }

    protected void setIsRunning(boolean running){
        m_isRunning = running;
    }
}
