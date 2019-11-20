package app.utilities;

import app.exceptions.ErisAppRuntimeException;
import app.models.Task;
import app.models.entities.TaskEntity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ObjectUtilTest {

    @Test
    void copyProperties() {
        Task task = new Task();
        task.setAddedBy("Jeff");
        task.setId(123);
        task.setDescription("Sample description ...");
        task.setName("Name of Task");

        TaskEntity taskEntity = new TaskEntity();

        ObjectUtil.copyProperties(task, taskEntity);

        assertEquals("Jeff", taskEntity.getAddedBy());
        assertEquals(123, taskEntity.getId());
        assertEquals("Sample description ...", taskEntity.getDescription());
        assertEquals("Name of Task", taskEntity.getName());
    }

    @Test
    void copyProperties_nullTest(){
        Task task = new Task();
        ErisAppRuntimeException e = assertThrows(ErisAppRuntimeException.class, () -> ObjectUtil.copyProperties(task, null));
        assertEquals("source or target cannot be null", e.getMessage());
    }
}