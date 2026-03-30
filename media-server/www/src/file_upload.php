<?php

require_once "simple_image.php";

/**
 * Manages upload and copy of files uploaded
 * @param string $file_field File field name
 * @param string $destination
 * @return true|FileUploadException True on OK | FileUploadException
 */

class FileUploadException extends RuntimeException {}

function file_upload(string $file_field, string $directory, string $id)
{
    define('MAX_FILE_SIZE', file_upload_max_size());
    define('MAX_FILE_SIZE_HUMAN', human_file_size(MAX_FILE_SIZE));
    $destination = $directory . $id;

    // If this request falls under any of them, treat it invalid.
    if (!isset($_FILES[$file_field]['error']) || is_array($_FILES[$file_field]['error'])) {
        throw new FileUploadException('Invalid parameters');
    }

    // Check $_FILES[$file_field]['error'] value.
    switch ($_FILES[$file_field]['error']) {
        case UPLOAD_ERR_OK:
            break;
        case UPLOAD_ERR_NO_FILE:
            throw new FileUploadException('No file sent');
        case UPLOAD_ERR_INI_SIZE:
        case UPLOAD_ERR_FORM_SIZE:
            throw new FileUploadException("The file size exceeds the maximum allowed size (Limit: MAX_FILE_SIZE_HUMAN)");
        default:
            throw new FileUploadException('Unknow error');
    }

    // Double check file size
    if ($_FILES[$file_field]['size'] > MAX_FILE_SIZE) {
        throw new FileUploadException("The file size exceeds the maximum allowed size (Limit: MAX_FILE_SIZE_HUMAN)");
    }

    // Move original to directory
    if (!move_uploaded_file($_FILES[$file_field]['tmp_name'], "$destination")) {
        throw new FileUploadException('Unable to move the file.');
    }

    return $destination;
}

function parse_size($size)
{
    $unit = preg_replace('/[^bkmgtpezy]/i', '', $size); // Remove the non-unit characters from the size.
    $size = preg_replace('/[^0-9\.]/', '', $size); // Remove the non-numeric characters from the size.
    if ($unit) {
        // Find the position of the unit in the ordered string which is the power of magnitude to multiply a kilobyte by.
        return round($size * pow(1024, stripos('bkmgtpezy', $unit[0])));
    } else {
        return round($size);
    }
}

function file_upload_max_size()
{
    static $max_size = -1;

    if ($max_size < 0) {
        // Start with post_max_size.
        $post_max_size = parse_size(ini_get('post_max_size'));
        if ($post_max_size > 0) {
            $max_size = $post_max_size;
        }

        // If upload_max_size is less, then reduce. Except if upload_max_size is
        // zero, which indicates no limit.
        $upload_max = parse_size(ini_get('upload_max_filesize'));
        if ($upload_max > 0 && $upload_max < $max_size) {
            $max_size = $upload_max;
        }
    }
    return $max_size;
}

function human_file_size($size, $unit = "")
{
    if ((!$unit && $size >= 1 << 30) || $unit == "Go")
        return number_format($size / (1 << 30), 2) . "Go";
    if ((!$unit && $size >= 1 << 20) || $unit == "Mo")
        return number_format($size / (1 << 20), 2) . "Mo";
    if ((!$unit && $size >= 1 << 10) || $unit == "Ko")
        return number_format($size / (1 << 10), 2) . "Ko";
    return number_format($size) . " octets";
}