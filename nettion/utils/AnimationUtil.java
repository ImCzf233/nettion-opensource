package nettion.utils;

public class AnimationUtil {
   public static float moveUD(float current, float end, float smoothSpeed, float minSpeed) {
      boolean larger = end > current;
      if (smoothSpeed < 0.0f) {
         smoothSpeed = 0.0f;
      } else if (smoothSpeed > 1.0f) {
         smoothSpeed = 1.0f;
      }
      if (minSpeed < 0.0f) {
         minSpeed = 0.0f;
      } else if (minSpeed > 1.0f) {
         minSpeed = 1.0f;
      }
      float movement = (end - current) * smoothSpeed;
      if (movement > 0) {
         movement = Math.max(minSpeed, movement);
         movement = Math.min(end - current, movement);
      } else if (movement < 0) {
         movement = Math.min(-minSpeed, movement);
         movement = Math.max(end - current, movement);
      }
      if (larger){
         if (end <= current + movement){
            return end;
         }
      }else {
         if (end >= current + movement){
            return end;
         }
      }
      return current + movement;
   }
   public static double moveUD(double current, double end, float smoothSpeed, float minSpeed) {
      boolean larger = end > current;
      if (smoothSpeed < 0.0f) {
         smoothSpeed = 0.0f;
      } else if (smoothSpeed > 1.0f) {
         smoothSpeed = 1.0f;
      }
      if (minSpeed < 0.0f) {
         minSpeed = 0.0f;
      } else if (minSpeed > 1.0f) {
         minSpeed = 1.0f;
      }
      double movement = (end - current) * smoothSpeed;
      if (movement > 0) {
         movement = Math.max(minSpeed, movement);
         movement = Math.min(end - current, movement);
      } else if (movement < 0) {
         movement = Math.min(-minSpeed, movement);
         movement = Math.max(end - current, movement);
      }
      if (larger){
         if (end <= current + movement){
            return end;
         }
      }else {
         if (end >= current + movement){
            return end;
         }
      }
      return current + movement;
   }
}
