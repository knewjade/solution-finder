package tetfu;

 class ColoredFieldFactory {
     public static ColoredField createField(int maxHeight) {
         return new ArrayColoredField(maxHeight);
     }
 }
