package test.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import test.opengl.Camera.ArcBall;
import test.opengl.Math.Quaternion;

/**
 * Created by user on 17/09/2017.
 */
class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private ArcBall m_camera;


    public MyGLSurfaceView(Context context){
        super(context);
        m_camera = new ArcBall();
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mRenderer = new MyGLRenderer(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                Quaternion quat = m_camera.updatePosition(x, y, getWidth(), getHeight(), mRenderer.getRotationMatrix());
                //float[] rotation = m_camera.updatePositionMat(x, y, getWidth(), getHeight(), mRenderer.getRotationMatrix());
                mRenderer.rotate(quat);
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                mRenderer.setAngle(
                        mRenderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
                break;
            default:
                m_camera.setPicked(false);
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}