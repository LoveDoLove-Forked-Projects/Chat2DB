package ai.chat2db.plugin.redis.model;

import lombok.Data;

@Data
public class SetValue extends Action{
   private String value;


   @Override
   public String toString() {
      return value;
   }
}
