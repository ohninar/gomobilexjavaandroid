package br.com.raninho.basicjava;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity implements GLSurfaceView.Renderer {

    private final int coordsPerVertex = 3;
    private final int vertexCount = 3;
    int program = 0;
    float green = 0.0f;

    float touchX = 0.0f;
    float touchY = 0.0f;

    int width = 0;
    int height = 0;

    float[] triangleData =
            {
                     0.0f,  0.4f, 0.0f,
                     0.0f,  0.0f, 0.0f,
                     0.4f,  0.0f, 0.0f,
            };

    String vertexShaderSource = "#version 100"+
            "uniform vec2 offset; "+
            "attribute vec4 position; "+
            "void main() {"+
            "    vec4 offset4 = vec4(2.0 * offset.x - 1.0, 1.0 - 2.0 * offset.y, 0, 0);"+
            "    gl_Position = position + offset4; "+
            "}";

    String vertexShaderSource2 = "#version 100"+
            "uniform vec2 translate;"+
            "attribute vec4 position;"+
            "void main() {"+
            "    gl_Position = position + vec4(translate.x, translate.y, 0.0, 0.0);"+
            "}";
    String fragmentShaderSource = "#version 100"+
            "precision mediump float;"+
            "uniform vec4 color;"+
            "void main() {"+
            "    gl_FragColor = color;"+
            "}";

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        touchX = e.getX();
        touchY = e.getY();

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView glView = new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.setRenderer(this);
        setContentView(glView);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderSource);
        GLES20.glCompileShader(vertexShader);
        String vertexShaderCompleLog = GLES20.glGetShaderInfoLog(vertexShader);

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderSource);
        GLES20.glCompileShader(fragmentShader);
        String fragmentShaderCompleLog = GLES20.glGetShaderInfoLog(fragmentShader);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glBindAttribLocation(program, 0, "position");
        GLES20.glLinkProgram(program);
        String programLinkLog = GLES20.glGetProgramInfoLog(program);

        GLES20.glUseProgram(program);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        GLES20.glViewport(0, 0, i, i1);
        width = i;
        height = i1;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        green += 0.01;
        if (green > 1.0) {
            green = 0.0f;
        }

        GLES20.glUniform4f(GLES20.glGetUniformLocation(program, "color"), 0, green, 0, 1);
        GLES20.glUniform2f(GLES20.glGetUniformLocation(program, "offset"), (float)touchX/width, (float)touchY/height);

        ByteBuffer triangleDataByteBuffer = ByteBuffer.allocateDirect(triangleData.length * 4);
        triangleDataByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer triangleBuffer = triangleDataByteBuffer.asFloatBuffer();
        triangleBuffer.put(triangleData);
        triangleBuffer.rewind();

        GLES20.glVertexAttribPointer(0, coordsPerVertex, GLES20.GL_FLOAT, false, 0, triangleBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }
}
