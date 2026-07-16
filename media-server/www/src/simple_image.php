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

const UNSUPPORTED_ERROR = "Unsupported image type",

class SimpleImage
{
    public $image;
    public $image_type;

    public function load($filename)
    {
        $image_info = getimagesize($filename);
        $this->image_type = $image_info[2];

        switch ($this->image_type) {
            case IMAGETYPE_JPEG:
                $this->image = imagecreatefromjpeg($filename);
                break;

            case IMAGETYPE_GIF:
                $this->image = imagecreatefromgif($filename);
                imagealphablending($this->image, false);
                imagesavealpha($this->image, true);
                break;

            case IMAGETYPE_PNG:
                $this->image = imagecreatefrompng($filename);
                imagealphablending($this->image, false);
                imagesavealpha($this->image, true);
                break;

            default:
                error_log(UNSUPPORTED_ERROR);
                break;
        }
    }

    public function save($filename, $compression = 75, $permissions = null)
    {
        switch ($this->image_type) {
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
                error_log(UNSUPPORTED_ERROR);
                break;
        }

        if ($permissions != null) {
            chmod($filename, $permissions);
        }
    }

    private function output()
    {
        switch ($this->image_type) {
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
                error_log(UNSUPPORTED_ERROR);
                break;
        }
    }

    private function getWidth()
    {
        return imagesx($this->image);
    }

    private function getHeight()
    {
        return imagesy($this->image);
    }

    public function resizeToHeight(int $height)
    {
        $ratio = $height / $this->getHeight();
        $width = intval($this->getWidth() * $ratio);
        $this->resize($width, $height);
    }

    public function resizeToWidth(int $width)
    {
        $ratio = $width / $this->getWidth();
        $height = intval($this->getheight() * $ratio);
        $this->resize($width, $height);
    }

    public function scale($scale)
    {
        $width =  intval($this->getWidth() * $scale / 100);
        $height = intval($this->getheight() * $scale / 100);
        $this->resize($width, $height);
    }

    public function resize(int $width, int $height)
    {
        $new_image = imagecreatetruecolor($width, $height);

        // Transparency for PNG
        if ($this->image_type == IMAGETYPE_PNG) {
            imagealphablending($new_image, false);
            imagesavealpha($new_image, true);

            $transparent = imagecolorallocatealpha($new_image, 0, 0, 0, 127);
            imagefill($new_image, 0, 0, $transparent);
        }

        // Transparency for GIF
        if ($this->image_type == IMAGETYPE_GIF) {
            $transparent_index = imagecolortransparent($this->image);

            if ($transparent_index >= 0) {
                $transparent_color = imagecolorsforindex($this->image, $transparent_index);
                $transparent_index_new = imagecolorallocate(
                    $new_image,
                    $transparent_color['red'],
                    $transparent_color['green'],
                    $transparent_color['blue']
                );

                imagefill($new_image, 0, 0, $transparent_index_new);
                imagecolortransparent($new_image, $transparent_index_new);
            }
        }

        imagecopyresampled($new_image, $this->image, 0, 0, 0, 0, $width, $height, $this->getWidth(), $this->getHeight());
        $this->image = $new_image;
    }
}
