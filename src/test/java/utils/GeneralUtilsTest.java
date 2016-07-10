package utils;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static utils.GeneralUtils.*;

import static org.junit.Assert.*;

/**
 * Created by roman on 11.07.2016.
 */
public class GeneralUtilsTest {
    @Test
    public void mapOrNull() throws Exception {
        assertNull(GeneralUtils.mapOrNull(null, (k)->k));
        assertThat(GeneralUtils.mapOrNull("test",k->k), is("test"));
    }

    @Test
    public void mapOrElse() throws Exception {
        assertThat(GeneralUtils.mapOrElse(null,k->k,"other"), is("other"));
        assertThat(GeneralUtils.mapOrElse("that",k->k,"other"), is("that"));
    }

}