package dungeonmania;

import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;

public class DeepCopy {
    public static Object copy(Object oldObj) throws Exception {
        ObjectOutputStream objectOuputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            objectOuputStream = new ObjectOutputStream(byteOutputStream);
            objectOuputStream.writeObject(oldObj);
            objectOuputStream.flush();
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
            objectInputStream = new ObjectInputStream(byteInputStream);
            return objectInputStream.readObject();
        } catch (Exception e) {
            throw(e);
        }
    }
}
