package app.utilities;

import app.exceptions.ErisAppRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
* @author: Jefferson Vivanco
* @summary: A class that implements some of the system util methods needed by the class.
* @test: ObjectUtilTest
* */
public class ObjectUtil {

    private ObjectUtil(){
        throw new IllegalStateException("Utility class");
    }

    private static Logger logger = LoggerFactory.getLogger(ObjectUtil.class);

    public static <S, T>  void copyProperties(S source, T target){
        try {
            if (source == null || target == null){
                throw new ErisAppRuntimeException("source or target cannot be null");
            }
            BeanInfo targetBeanInfo = Introspector.getBeanInfo(target.getClass());
            BeanInfo sourceBeanInfo = Introspector.getBeanInfo(source.getClass());
            PropertyDescriptor targetPds[] = targetBeanInfo.getPropertyDescriptors();
            PropertyDescriptor sourcePds[] = sourceBeanInfo.getPropertyDescriptors();

            for (PropertyDescriptor targetPd: targetPds){

                Method tPdWriteMethod = targetPd.getWriteMethod();
                if (tPdWriteMethod == null)
                    continue;

                PropertyDescriptor sourcePd = searchPropertyDescriptor(sourcePds, targetPd.getName());
                if (sourcePd == null)
                    continue;

                Method sPdReadMethod = sourcePd.getReadMethod();
                if (sPdReadMethod == null)
                    continue;

                if (targetPd.getPropertyType() != sourcePd.getPropertyType())
                    continue;

                Object sourceVal = sPdReadMethod.invoke(source);
                tPdWriteMethod.invoke(target, sourceVal);
            }
        } catch (IntrospectionException e){
            logger.error("Introspection error: {}", e.getMessage());
        } catch (IllegalAccessException | InvocationTargetException e2){
            logger.error("Illegal access error: {}", e2.getMessage());
        }

    }

    private static PropertyDescriptor searchPropertyDescriptor(PropertyDescriptor pds[], String pdName){
        for (PropertyDescriptor pd: pds){
            if (pd.getName().equals(pdName)){
                return pd;
            }
        }
        return null;
    }
}
