package com.testcase;

import com.employeepayroll.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NIOFileAPITest
{
    private static String HOME = System.getProperty("user.home");
    private static String PLAY_WITH_NIO = "TempPlayGround";

    @Test
    public void givenPath_WhenChecked_ThenConfirm() throws IOException
    {
        //check file exists
        Path homePath = Paths.get(HOME);
        Assert.assertTrue(Files.exists(homePath));

        //delete file and check file not exist
        Path playPath = Paths.get(HOME + "/" + PLAY_WITH_NIO);
        if (Files.exists(playPath)) FileUtils.deleteFiles(playPath.toFile());
        Assert.assertTrue(Files.notExists(playPath));

        //create directory
    }
}
