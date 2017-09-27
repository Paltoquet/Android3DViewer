package test.opengl.Utils;

import java.util.ArrayList;

import test.opengl.ObjLoader.Face;
import test.opengl.ObjLoader.FaceVertex;
import test.opengl.ObjLoader.VertexGeometric;
import test.opengl.ObjLoader.VertexNormal;
import test.opengl.ObjLoader.VertexTexture;

/**
 * Created by user on 23/09/2017.
 */
public class VBO {


    public static int VERTEX_COORD  = 3;
    public static int TEXTURE_COORD = 2;
    public static int NORMAL_COORD  = 3;
    public static int STRIDE = VERTEX_COORD + TEXTURE_COORD + NORMAL_COORD;
    public float[] vbo;
    public float[] pos;
    public float[] tex;
    public float[] norm;

    public VBO(ArrayList<FaceVertex> facesVertex){
        FaceVertex vert;
        VertexGeometric v;
        VertexTexture vt;
        VertexNormal vn;
        int size = facesVertex.size();
        vbo = new float[size * STRIDE];

        for(int i=0; i<size; i++) {
            vert = facesVertex.get(i);
            v =  vert.v;
            vt = vert.t;
            vn = vert.n;
            vbo[i*STRIDE + 0] = v.x;
            vbo[i*STRIDE + 1] = v.y;
            vbo[i*STRIDE + 2] = v.z;
            vbo[i*STRIDE + 3] = vt.u;
            vbo[i*STRIDE + 4] = -1* vt.v;
            vbo[i*STRIDE + 5] = vn.x;
            vbo[i*STRIDE + 6] = vn.y;
            vbo[i*STRIDE + 7] = vn.z;
        }
    }

    /*public VBO(ArrayList<Face> faces){
        Face face;
        FaceVertex vert;
        VertexGeometric v;
        VertexTexture vt;
        VertexNormal vn;
        ArrayList<FaceVertex> vertices;
        int size = faces.size();
        vbo = new float[size*(3+3+2)*3];
        pos = new float[size*3*3];
        tex =  new float[size*3*2];
        norm = new float[size*3*3];
        for(int i=0; i<size; i++){
            face = faces.get(i);
            vertices = face.vertices;
            for(int j=0; j<3; j++){
                vert = vertices.get(j);
                v = vert.v;
                vt = vert.t;
                vn = vert.n;
                vbo[i*24+j*8+0] = v.x;
                vbo[i*24+j*8+1] = v.y;
                vbo[i*24+j*8+2] = v.z;
                vbo[i*24+j*8+3] = vt.u;
                vbo[i*24+j*8+4] = vt.v;
                vbo[i*24+j*8+5] = vn.x;
                vbo[i*24+j*8+6] = vn.y;
                vbo[i*24+j*8+7] = vn.z;
                /*pos[i*9+j*3+0] = v.x;
                pos[i*9+j*3+1] = v.y;
                pos[i*9+j*3+2] = v.z;
                tex[i*6+j*2+0] = vt.u;
                tex[i*6+j*2+1] = vt.v;
                norm[i*9+j*3+0] = vn.x;
                norm[i*9+j*3+1] = vn.y;
                norm[i*9+j*3+2] = vn.z;

            }
        }
    }*/
}
