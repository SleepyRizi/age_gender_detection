package com.example.gender_age_detection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.Calendar;
import java.util.Date;

public class age_gender_detection {

    private Interpreter interpreter;
    // define input size
    private int INPUT_SIZE;
    // define height and width of original frame
    private int height=0;
    private int width=0;
    Date currentTime = Calendar.getInstance().getTime();
    // now define Gpudelegate
    // it is use to implement gpu in interpreter
    private GpuDelegate gpuDelegate=null;

    // now define cascadeClassifier for face detection
    private CascadeClassifier cascadeClassifier;

        age_gender_detection(AssetManager assetManager, Context context, String modelPath,int inputSize) throws IOException {
            INPUT_SIZE=inputSize;
            // set GPU for the interpreter

            Interpreter.Options options=new Interpreter.Options();
            gpuDelegate=new GpuDelegate();
            // add gpuDelegate to option
            options.addDelegate(gpuDelegate);
            // now set number of threads to options
            options.setNumThreads(4); // set this according to your phone
            // this will load model weight to interpreter

            interpreter=new Interpreter(loadModelFile(assetManager,modelPath),options);

                Toast.makeText(context,"Model is loaded",Toast.LENGTH_SHORT).show();


            // if model is load print
            Log.d("age_gender_detection","Model is loaded");

            // now we will load haarcascade classifier
            try {
                // define input stream to read classifier
                InputStream is=context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
                // create a folder
                File cascadeDir=context.getDir("cascade",Context.MODE_PRIVATE);
                // now create a new file in that folder
                File mCascadeFile=new File(cascadeDir,"haarcascade_frontalface_alt");
                // now define output stream to transfer data to file we created
                FileOutputStream os=new FileOutputStream(mCascadeFile);
                // now create buffer to store byte
                byte[] buffer=new byte[4096];
                int byteRead;
                // read byte in while loop
                // when it read -1 that means no data to read
                while ((byteRead=is.read(buffer)) !=-1){
                    // writing on mCascade file
                    os.write(buffer,0,byteRead);

                }
                // close input and output stream
                is.close();
                os.close();
                cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                // if cascade file is loaded print
                Log.d("age_gender_detection","Classifier is loaded");


            }
            catch (IOException e){
                e.printStackTrace();
            }


    }
    public Mat recognizeImage(Mat mat_image,Context context){
            Mat a=mat_image.t();
            Core.flip(a,mat_image,1);
            a.release();
            //Core.flip(mat_image.t(),mat_image,1);


            Mat grayscaleImage= new Mat();
            Imgproc.cvtColor(mat_image,grayscaleImage,Imgproc.COLOR_RGB2GRAY);

            height = grayscaleImage.height();
            width= grayscaleImage.width();


            int absoluteFaceSize=(int)(height*0.1);
            // now create MatofRect to store face
            MatOfRect faces=new MatOfRect();
            // check if cascadeClassifier is loaded or not
            if(cascadeClassifier !=null){
                // detect face in frame
                //                                  input         output
                cascadeClassifier.detectMultiScale(grayscaleImage,faces,1.1,2,2,
                        new Size(absoluteFaceSize,absoluteFaceSize),new Size());
                // minimum size
            }


            Rect[] faceArray = faces.toArray();
            for (int i=0; i< faceArray.length; i++){
                Imgproc.rectangle(mat_image,faceArray[i].tl(),faceArray[i].br(),new Scalar(0,255,0,255),2);
                Rect roi = new Rect((int)faceArray[i].tl().x,(int)faceArray[i].tl().y,
                        ((int)faceArray[i].br().x)-(int)(faceArray[i].tl().x),
                        ((int)faceArray[i].br().y)-(int)(faceArray[i].tl().y));

                Mat cropped = new Mat(grayscaleImage,roi);
                Mat cropped_rgba= new Mat(mat_image,roi);

                Bitmap bitmap = null;
                bitmap = Bitmap.createBitmap(cropped_rgba.cols(),cropped_rgba.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(cropped_rgba,bitmap);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,96,96,false);

                ByteBuffer byteBuffer = converBitmapToByteBuffer(scaledBitmap);

                Object[] input= new Object[1];
                input[0] = byteBuffer;

                Map<Integer,Object> output_map= new TreeMap<>();
                float[][] age = new float[1][1];
                float[][] gender = new float[1][1];
                output_map.put(0,age);
                output_map.put(1,gender);


                interpreter.runForMultipleInputsOutputs(input,output_map);

                //System.out.println(output_map.values().toString());
                Object age_out= output_map.get(0);
                Object gender_out= output_map.get(1);

                int age_value = (int) (float) Array.get(Array.get(age_out,0),0);
                float gender_val= (float) Array.get(Array.get(gender_out,0),0);



                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                if(gender_val>0.5){
                    editor.clear();
                    editor.putString("age", String.valueOf(age_value));
                    editor.putString("gender", "Female");
                    editor.putString("date", String.valueOf(currentTime));
                    System.out.println(currentTime);
                    editor.apply();
                }
                if(gender_val<0.5){
                    editor.clear();
                    editor.putString("age", String.valueOf(age_value));
                    editor.putString("gender", "Male");
                    editor.putString("date", String.valueOf(currentTime));
                    editor.apply();

                }



                if (gender_val > 0.5) {

                    Imgproc.putText(cropped_rgba,"Female, "+age_value,new Point(10,20),1,1.7,new Scalar(255,0,0,255),4);




                }else{
                    Imgproc.putText(cropped_rgba,"Male, "+age_value,new Point(10,20),1,1.7,new Scalar(0,0,255,255),4);
                    

                }
                Log.d("age_gender_detection","Output"+age_value+","+gender_val);

                cropped_rgba.copyTo(new Mat(mat_image,roi));

            }
            Mat b=mat_image.t();
            Core.flip(b,mat_image,0);
            b.release();

            //Core.flip(mat_image.t(),mat_image,0);
            return mat_image;
    }

    private ByteBuffer converBitmapToByteBuffer(Bitmap scaledBitmap) {
        ByteBuffer byteBuffer;
        int size_image = 96;
        byteBuffer = ByteBuffer.allocateDirect(4 * size_image * size_image * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intvalues = new int[size_image * size_image];
        scaledBitmap.getPixels(intvalues, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < size_image; i++){
            for (int j = 0; j < size_image; j++) {
                final int val = intvalues[pixel++];
                byteBuffer.putFloat((((val >> 16) & 0xFF)) / 255.0f);
                byteBuffer.putFloat((((val >> 8) & 0xFF)) / 255.0f);
                byteBuffer.putFloat(((val & 0xFF)) / 255.0f);
            }
    }
        return  byteBuffer;
    }



    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelpath) throws IOException{
        AssetFileDescriptor assetFileDescriptor = assetManager.openFd(modelpath);
        FileInputStream inputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset= assetFileDescriptor.getStartOffset();
        long declaredLength= assetFileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);
    }

}
