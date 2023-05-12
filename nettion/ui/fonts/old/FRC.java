package nettion.ui.fonts.old;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;

public class FRC
{
    public static boolean betterFontsEnabled = true;

    public static String bidiReorder(IBFFontRenderer font, String text)
    {
        if (betterFontsEnabled && font.getStringCache() != null)
        {
            return text;
        }

        try
        {
            Bidi bidi = new Bidi((new ArabicShaping(8)).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (ArabicShapingException var3)
        {
            return text;
        }
    }
}
