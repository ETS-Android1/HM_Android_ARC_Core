package com.healthymedium.arc.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.healthymedium.arc.utilities.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.PathSegmentTypeAdapter;
import com.healthymedium.arc.time.DateTimeTypeAdapter;
import com.healthymedium.arc.time.LocalTimeTypeAdapter;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheManager {

    private static final String tag = "CacheManager";
    private static CacheManager instance;

    private class Cache{
        String content;
        File file;
        boolean read;
        boolean saved;

        Cache(){
            content = "{}";
            file = null;
            read = false;
            saved = false;
        }
    }

    private Map<String, File> bitmaps = new HashMap<>();
    private Map<String, Cache> map = new HashMap<>();

    private File cacheDir;
    private Gson objectGson;

    private CacheManager(Context context) {

        cacheDir = context.getCacheDir();

        // collect bitmaps
        FileFilter bitmapFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains(".png");
            }
        };

        File[] bitampFiles = cacheDir.listFiles(bitmapFilter);
        int bitmapCount = bitampFiles.length;

        for(int i=0;i<bitmapCount;i++) {
            File file = bitampFiles[i];
            String name = file.getName().replace(".png","");
            Log.d(tag,"found bitmap = "+name);
            bitmaps.put(name,file);
        }

        // everthing else should be json objects
        FileFilter objectFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.getName().contains(".png");
            }
        };
        File[] files = cacheDir.listFiles(objectFilter);
        int count = files.length;

        for(int i=0;i<count;i++) {
            File file = files[i];
            Cache cache = new Cache();
            cache.file = file;
            String name  = file.getName();
            Log.d(tag,"found object = "+name);
            map.put(file.getName(), cache);
        }

        buildObjectGson();
    }

    public static synchronized void initialize(Context context) {
        instance = new CacheManager(context);
    }

    public static synchronized CacheManager getInstance() {
        return instance;
    }

    private void buildObjectGson(){
        objectGson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriAdapter())
                .registerTypeAdapter(List.class, new ListTypeAdapter())
                .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(PathSegment.class,new PathSegmentTypeAdapter())
                .registerTypeAdapter(BaseData.class, new BaseDataTypeAdapter())
                .create();
    }

    public Gson getGson() {
        return objectGson;
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public void remove(String key) {
        Log.i(tag,"remove "+key);
        if(map.containsKey(key)){
            File file = map.get(key).file;
            file.delete();
            map.remove(key);
        } else if(bitmaps.containsKey(key)){
            File file = bitmaps.get(key);
            file.delete();
            bitmaps.remove(key);
        }
    }

    public void removeAll() {
        Log.i(tag,"removeAll");
        for(Map.Entry<String,Cache> entry : map.entrySet()){
            entry.getValue().file.delete();
        }
        map.clear();
        for(Map.Entry<String,File> entry : bitmaps.entrySet()){
            entry.getValue().delete();
        }
        bitmaps.clear();
    }

    public void save(String key) {
        if(map.containsKey(key)){
            Cache cache = map.get(key);
            if(cache.saved){
               return;
            }
            boolean written = writeTextFile(cache.file,cache.content);
            Log.i(tag,"key("+key+") save = "+written);
            if(written){
                cache.saved = true;
            }
        }
    }

    public void saveAll() {
        Log.i(tag,"saveAll");
        for(Map.Entry<String,Cache> entry : map.entrySet()){
            Cache cache = entry.getValue();
            if(!cache.saved) {
                boolean written = writeTextFile(cache.file, cache.content);
                Log.i(tag,"key( "+entry.getKey()+") save = "+written);
                if(written){
                    cache.saved = true;
                }
            }
        }
    }

    public void putObject(String key, Object object) {
        Log.i(tag,"putObject("+key+")");
        if(object==null){
            Log.i(tag,"invalid object, failed to store");
            return;
        }
        if(!map.containsKey(key)){
            File file = new File(cacheDir, key);
            if(!file.exists()){
                try {
                    file.createNewFile();
                    writeTextFile(file,"{}");
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            Cache cache_t = new Cache();
            cache_t.file = file;
            cache_t.read = true;
            map.put(key,cache_t);
        }

        Cache cache = map.get(key);
        String content = objectGson.toJson(object);
        if(!cache.content.equals(content)){
            cache.content = content;
            cache.saved = false;
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        Log.i(tag,"getObject("+key+")");
        if(!map.containsKey(key)){
            try {
                return clazz.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new UnsupportedOperationException(e.getMessage());
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new UnsupportedOperationException(e.getMessage());
            }
        }
        Cache cache = map.get(key);
        if(!cache.read){
            cache.content = readTextFile(cache.file);
            cache.read = true;
        }
        return objectGson.fromJson(cache.content, clazz);
    }

    public File getFile(String key) {
        Log.i(tag,"getFile("+key+")");
        if(map.containsKey(key)){
            return map.get(key).file;
        }
        if(bitmaps.containsKey(key)){
            return bitmaps.get(key);
        }
        File file = new File(cacheDir, key);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        Cache cache_t = new Cache();
        cache_t.file = file;
        cache_t.read = true;
        map.put(key,cache_t);
        return null;
    }

    public Bitmap getBitmap(String key) {
        Log.i(tag,"getBitmap("+key+")");
        if(!bitmaps.containsKey(key)){
            return null;
        }
        return readBitmap(bitmaps.get(key));
    }

    public boolean putBitmap(String key, Bitmap bitmap, int quality) {
        Log.i(tag, "putBitmap(" + key + ")");
        if (!bitmaps.containsKey(key)) {
            File file = new File(cacheDir, key+".png");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            bitmaps.put(key, file);
        }
        return writeBitmap(bitmaps.get(key), bitmap, quality);
    }

    private String readTextFile(File file){
        Log.i(tag,"readTextFile("+file.getName()+")");
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                contentBuilder.append(currentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
        return contentBuilder.toString();
    }

    private boolean writeTextFile(File file, String string){
        Log.i(tag,"writeTextFile("+file.getName()+")");
        try {
            RandomAccessFile stream = new RandomAccessFile(file, "rw");
            stream.setLength(0);
            FileChannel channel = stream.getChannel();
            byte[] strBytes = string.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
            buffer.put(strBytes);
            buffer.flip();
            channel.write(buffer);
            stream.close();
            channel.close();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Bitmap readBitmap(File file){
        Log.i(tag,"readBitmap("+file.getName()+")");
        String path =  file.getPath();
        return BitmapFactory.decodeFile(path);
    }

    private boolean writeBitmap(File file, Bitmap bitmap, int quality){
        Log.i(tag,"writeBitmap("+file.getName()+")");

        FileOutputStream fileOutputStream = null;
        boolean compressed = false;
        try {
            fileOutputStream = new FileOutputStream(file);
            compressed = bitmap.compress(Bitmap.CompressFormat.PNG, quality, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return compressed;
    }




    private class UriAdapter extends TypeAdapter<Uri> {
        @Override
        public void write(JsonWriter out, Uri uri) throws IOException {
            if (uri != null) {
                out.value(uri.toString());
            }
            else {
                out.nullValue();
            }
        }

        @Override
        public Uri read(JsonReader in) throws IOException {
            return Uri.parse(in.nextString());
        }
    }

    public class ListTypeAdapter implements JsonDeserializer<List> {

        @Override
        public List deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List list = new ArrayList();
            Type type = ((ParameterizedType)typeOfT).getActualTypeArguments()[0];

            if (json.isJsonArray()) {
                for (JsonElement element : json.getAsJsonArray()) {
                    list.add(context.deserialize(element,type));
                }
            }
            return list;
        }

    }

    public class BaseDataTypeAdapter implements JsonDeserializer<BaseData>, JsonSerializer<BaseData>
    {
        @Override
        public BaseData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            try {
                Class actualClass = null;
                actualClass = Class.forName(obj.get("actual_class").getAsString());
                Object data = objectGson.fromJson(obj.get("data"), actualClass);

                if(BaseData.class.isAssignableFrom(data.getClass())) {
                    return (BaseData) data;
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return new BaseData();
        }

        @Override
        public JsonElement serialize(BaseData src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("data", objectGson.toJsonTree(src));
            result.addProperty("actual_class",  src.getClass().getName());
            return result;
        }
    }

}
