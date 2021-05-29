package peash.esales.Fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import Adi.esales.R;
import peash.esales.CustomView.DrawLineCanvas;
//import peash.esales.R;


public class Canvas_Fragment extends Fragment {

    private DrawLineCanvas dlc;
    Button btnSaveImage, btnClear, btnSign;
    Bitmap b;
    String s;
    int width,height;
    byte[] byteArray;
    OutputStream outputStream;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_canvas, container, false);

        s = getArguments().getString("temp");



        btnSaveImage = v.findViewById(R.id.btn_save_image);
        btnClear = v.findViewById(R.id.btn_Clear);
        btnSign = v.findViewById(R.id.btnSign);
        final ImageView iv = v.findViewById(R.id.iv);
        dlc = v.findViewById(R.id.dlc);

        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //---- get Device Screen height and width-----//
//                DisplayMetrics displayMetrics = new DisplayMetrics();
//                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                int height = displayMetrics.heightPixels;
//                int width = displayMetrics.widthPixels;
//
//
//                v = dlc;
//                v.setDrawingCacheEnabled(true);
//                v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//
//                Bitmap bitmap = Bitmap.createBitmap(width/*width*/, 300/*height*/, Bitmap.Config.ARGB_4444);
//                bitmap.eraseColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
//                Canvas canvas = new Canvas(bitmap);
//                v.draw(canvas);
//                b = bitmap;     //for saving "b" to the sdcard
//                iv.setImageBitmap(b);
                if (b!=null)
                {
                    BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
                    b = drawable.getBitmap();

                    File filepath = Environment.getExternalStorageDirectory();
                    File dir = new File(filepath.getAbsolutePath() + "/Sign/");
                    dir.mkdir();
                    File file = new File(dir, System.currentTimeMillis() + ".png");
                    try {
                        outputStream = new FileOutputStream(file);
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                    b.compress(Bitmap.CompressFormat.PNG,80,outputStream);
                    Toast.makeText(getActivity().getApplicationContext(), "Image Save", Toast.LENGTH_SHORT).show();
//                    Canvas_Fragment homefragment = new Canvas_Fragment();
//                    android.support.v4.app.FragmentTransaction homeFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    homeFragmentTransaction.replace(R.id.frame,homefragment);
//                    homeFragmentTransaction.commit();
                    try {
                        outputStream.flush();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    try {
                        outputStream.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please make sign fast", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //---- get Device Screen height and width-----//
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;


                v = dlc;
                v.setDrawingCacheEnabled(true);
                v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                Bitmap bitmap = Bitmap.createBitmap(width/*width*/, height/*height*/, Bitmap.Config.ARGB_4444);
                bitmap.eraseColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
                Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                b = bitmap;     //for saving "b" to the sdcard

                iv.setImageBitmap(b);

                //----------Convert bitmapToByte-------//
                int size = b.getRowBytes() * b.getHeight();
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                b.copyPixelsToBuffer(byteBuffer);
                byteArray = byteBuffer.array();
                //-------------Convert bitmapToString------------
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
                byte [] b=baos.toByteArray();
                String temp= Base64.encodeToString(b, Base64.DEFAULT);
                //-------------------------------------//

//                Bitmap bitmapObtained =b;
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmapObtained.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                byte[] byteArray = stream.toByteArray();

                if(s=="Sales")
                {
                    //---------Pass Bitmap as Byte array[b]-----//
                    Fragment sf=new SalesFragment();
                    FragmentManager fm=getFragmentManager();
                    FragmentTransaction ft=fm.beginTransaction();
                    Bundle args = new Bundle();
                    args.putByteArray("byteArray", b);
                    args.putString("temp", temp);
                    sf.setArguments(args);
                    ft.replace(R.id.frame, sf);
                    ft.commit();
                    //----------------------------------------//
                }
                else
                {
                    //---------Pass Bitmap as Byte array[b]-----//
                    Fragment of=new OrderFragment();
                    FragmentManager fmo=getFragmentManager();
                    FragmentTransaction oft=fmo.beginTransaction();
                    Bundle argsOrder = new Bundle();
                    argsOrder.putByteArray("byteArray", b);
                    argsOrder.putString("temp", temp);
                    of.setArguments(argsOrder);
                    oft.replace(R.id.frame, of);
                    oft.commit();
                    //----------------------------------------//
                }

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bitmpap bmp; // not null
//                if (b!=null)
//                {
//                    b.recycle();
//                    b = null;
//                    iv.setImageBitmap(b);
//                }
//                else {
                    //Toast.makeText(getActivity().getApplicationContext(), "Already clear", Toast.LENGTH_SHORT).show();

                Canvas_Fragment canvas_fragment1 = new Canvas_Fragment();
                android.support.v4.app.FragmentTransaction test1fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString("temp", s);
                canvas_fragment1.setArguments(args);
                test1fragmentTransaction.replace(R.id.frame,canvas_fragment1);
                test1fragmentTransaction.commit();

//                    Canvas_Fragment homefragment = new Canvas_Fragment();
//                    android.support.v4.app.FragmentTransaction homeFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    homeFragmentTransaction.replace(R.id.frame,homefragment);
//                    homeFragmentTransaction.commit();
//                }

            }
        });
        return v;
    }

    public void bitmapToByte()
    {
        width = b.getWidth();
        height = b.getHeight();

        int size = b.getRowBytes() * b.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        b.copyPixelsToBuffer(byteBuffer);
        byteArray = byteBuffer.array();
    }

    public void ByteToBitmap()
    {
        Bitmap.Config configBmp = Bitmap.Config.valueOf(b.getConfig().name());
        Bitmap bitmap_tmp = Bitmap.createBitmap(width, height, configBmp);
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        bitmap_tmp.copyPixelsFromBuffer(buffer);
        b=bitmap_tmp;
    }



    //your canvas as per your requirement
//    public class CanvasWithText extends View {
//
//        private Paint pBackground, pText;
//
//        private Bitmap bitmap;
//
//        public CanvasWithText(Context context, Bitmap bitmap) {
//            super(context);
//            this.bitmap = bitmap;
//            pBackground = new Paint();
//            pText = new Paint();
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            super.onDraw(canvas);
//
//            pBackground.setColor(Color.WHITE);
//            canvas.drawRect(0, 0, 512, 512, pBackground);
//            canvas.drawBitmap(b, 0, 0, pBg);
//            canvas.drawPath(touchPath, pLine);
//        }
//    }
}
