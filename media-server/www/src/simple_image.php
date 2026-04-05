<?php

/*
* File: SimpleImage.php
* Author: Simon Jarvis (modified by ReVoiceChat contibutor)
* Copyright: 2006 Simon Jarvis
* Date: 08/11/06
* Link: http://www.white-hat-web-design.co.uk/blog/resizing-images-with-php/
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details:
* http://www.gnu.org/licenses/gpl.html
*
*/

class SimpleImage
{
    var $image;
    var $image_type;

    function load($filename)
    {
        $image_info = getimagesize($filename);
        $this->image_type = $image_info[2];

        imagealphablending($this->image, false);
        imagesavealpha($this->image, true);

        switch ($this->image_type) {
            case IMAGETYPE_JPEG:
                $this->image = imagecreatefromjpeg($filename);
                break;

            case IMAGETYPE_GIF:
                $this->image = imagecreatefromgif($filename);
                break;

            case IMAGETYPE_PNG:
                $this->image = imagecreatefrompng($filename);
                break;

            default:
                error_log("Unsupported image type");
                break;
        }
    }

    function save($filename, $image_type = IMAGETYPE_JPEG, $compression = 75, $permissions = null)
    {
        imagealphablending($this->image, false);
        imagesavealpha($this->image, true);

        switch ($image_type) {
            case IMAGETYPE_JPEG:
                imagejpeg($this->image, $filename, $compression);
                break;

            case IMAGETYPE_GIF:
                imagegif($this->image, $filename);
                break;

            case IMAGETYPE_PNG:
                imagepng($this->image, $filename);
                break;

            default:
                error_log("Unsupported image type");
                break;
        }

        if ($permissions != null) {

            chmod($filename, $permissions);
        }
    }

    function output($image_type = IMAGETYPE_JPEG)
    {
        switch ($image_type) {
            case IMAGETYPE_JPEG:
                imagejpeg($this->image);
                break;

            case IMAGETYPE_GIF:
                imagegif($this->image);
                break;

            case IMAGETYPE_PNG:
                imagepng($this->image);
                break;

            default:
                error_log("Unsupported image type");
                break;
        }
    }

    function getWidth()
    {
        return imagesx($this->image);
    }

    function getHeight()
    {
        return imagesy($this->image);
    }

    function resizeToHeight($height)
    {
        $ratio = $height / $this->getHeight();
        $width = $this->getWidth() * $ratio;
        $this->resize($width, $height);
    }

    function resizeToWidth($width)
    {
        $ratio = $width / $this->getWidth();
        $height = $this->getheight() * $ratio;
        $this->resize($width, $height);
    }

    function scale($scale)
    {
        $width = $this->getWidth() * $scale / 100;
        $height = $this->getheight() * $scale / 100;
        $this->resize($width, $height);
    }

    function resize($width, $height)
    {
        $new_image = imagecreatetruecolor($width, $height);
        imagecopyresampled($new_image, $this->image, 0, 0, 0, 0, $width, $height, $this->getWidth(), $this->getHeight());
        $this->image = $new_image;
    }
}
