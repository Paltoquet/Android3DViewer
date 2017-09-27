package test.opengl.Camera;

import android.opengl.Matrix;
import android.util.Log;

import test.opengl.Math.Quaternion;
import test.opengl.Math.Vector3;
import test.opengl.Math.Vector4;

/**
 * Created by user on 24/09/2017.
 */
public class ArcBall {

    boolean picked;
    private float m_previousX;
    private float m_previousY;
    private Vector3 m_previousVector;

    public ArcBall(){
        m_previousX = 0;
        m_previousY = 0;
        picked = false;
    }

    public Quaternion updatePosition(float x, float y, int width, int height, float[] rotationMatrix){
        Quaternion result;
        Vector3 OP = new Vector3();
        float posX = (float)(x/width*2 - 1.0f);
        float posY = (float)(y/height*2 - 1.0f);
        OP.x = posX;
        OP.y = posY;
        float projectedVecSquared = posX * posX + posY * posY;
        if (projectedVecSquared <= 1) {
            //rayon is 1 we have the prjected vector on the XY plane we know the radius need the z component
            OP.z = (float) Math.sqrt(1 - projectedVecSquared);  // Pythagore
        }
        else {
            OP.z = 0;
        }
        OP.normalize();
        if(!picked){
            result = new Quaternion(1.0f,0.0f,0.0f,0.0f);
            picked = true;
        }
        else{
            float[] objectMatrice = new float[16];
            float[] rotationVector = new float[4];
            Vector3 perp = OP.cross(m_previousVector);
            Matrix.invertM(objectMatrice, 0, rotationMatrix, 0);
            Matrix.multiplyMV(rotationVector, 0, objectMatrice, 0, new Vector4(perp, 1).getAsArray(), 0);
            float angle = Math.min(OP.dot(m_previousVector), 1.0f);
            angle = Math.max((float)Math.acos(angle)*15,1.0f);
            Log.d("angle", String.valueOf(angle));
            result = new Quaternion(new Vector3(rotationVector[0],rotationVector[1], rotationVector[2]), angle);
        }
        m_previousX = posX;
        m_previousY = posY;
        m_previousVector = OP;
        Log.d("quat",String.valueOf(result));
        return result;
    }


    public float[] updatePositionMat(float x, float y, int width, int height, float[] rotationMatrix){
        float[] result = new float[16];
        Vector3 OP = new Vector3();
        float posX = (float)(x/width*2 - 1.0f);
        float posY = (float)(y/height*2 - 1.0f);
        OP.x = posX;
        OP.y = posY;
        float projectedVecSquared = posX * posX + posY * posY;
        if (projectedVecSquared <= 1) {
            //rayon is 1 we have the prjected vector on the XY plane we know the radius need the z component
            OP.z = (float) Math.sqrt(1 - projectedVecSquared);  // Pythagore
        }
        else {
            OP.z = 0;
        }
        OP.normalize();
        if(!picked){
            Matrix.setIdentityM(result, 0);
            picked = true;
        }
        else{
            float[] objectMatrice = new float[16];
            float[] rotationVector = new float[4];
            Vector3 perp = OP.cross(m_previousVector);
            Matrix.invertM(objectMatrice, 0, rotationMatrix, 0);
            Matrix.multiplyMV(rotationVector, 0, objectMatrice, 0, new Vector4(perp,1).getAsArray(), 0);
            float angle = Math.min(OP.dot(m_previousVector), 1.0f);
            angle = (float)Math.acos(angle);
            Log.d("angle", String.valueOf(angle));
            Matrix.setRotateM(result, 0, angle, rotationVector[0], rotationVector[1], rotationVector[2]);
        }
        m_previousX = posX;
        m_previousY = posY;
        m_previousVector = OP;
        Log.d("quat",String.valueOf(result));
        return result;
    }

    public void setPicked(boolean pick){
        picked = pick;
    }
}
