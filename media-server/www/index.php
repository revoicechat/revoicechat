<?php
require_once 'src/files.php';
require_once 'src/tools.php';

const CONTENT_TYPE_APPLICATION_JSON = "Content-Type: application/json";
const SUPPORTED_IMAGETYPE = [IMAGETYPE_JPEG, IMAGETYPE_GIF, IMAGETYPE_PNG];

$body = json_decode(file_get_contents('php://input'), true, 512, JSON_OBJECT_AS_ARRAY);

switch ($_SERVER["REQUEST_METHOD"]) {
    case 'GET':
        if (isset($_GET['maxfilesize'])) {
            require_once 'src/file_upload.php';
            header(CONTENT_TYPE_APPLICATION_JSON);
            echo json_encode(['maxFileSize' => file_upload_max_size()]);
            break;
        }

        if (isset($_GET['attachment']) && !empty($_GET['attachment'])) {
            if (isset($_GET['thumbnail'])) {
                rvc_read_file('attachments/thumbnail', $_GET['attachment']);
                break;
            }

            rvc_download_file('attachments', $_GET['attachment']);
            break;
        }

        if (isset($_GET['profiles']) && !empty($_GET['profiles'])) {
            rvc_read_file('profiles', $_GET['profiles']);
            break;
        }

        error_log("Invalid endpoint " . $_SERVER['REQUEST_URI'] . " Method " . $_SERVER["REQUEST_METHOD"]);
        http_response_code(400);
        break;

    case 'POST':
        if (isset($_GET['attachment']) && !empty($_GET['attachment'])) {
            post_attachment_upload($_GET['attachment']);
            break;
        }

        if (isset($_GET['profiles']) && !empty($_GET['profiles'])) {
            post_profile_upload($_GET['profiles']);
            break;
        }

        error_log("Invalid endpoint " . $_SERVER['REQUEST_URI'] . " Method " . $_SERVER["REQUEST_METHOD"]);
        http_response_code(400);
        break;

    default:
        error_log("Invalid endpoint " . $_SERVER['REQUEST_URI'] . " Method " . $_SERVER["REQUEST_METHOD"]);
        http_response_code(405);
        break;
}

exit;


function post_profile_upload(string $id)
{
    if (url_with_id("profiles", $matches)) {
        $id = $matches[1];
        $user = get_current_user_from_auth();

        if ($id != $user['id'] && $user['type'] != 'ADMIN') {
            echo json_encode(['error' => 'You cannot edit this profile', 'user' => $user]);
            http_response_code(401);
            return;
        }

        require_once('src/file_upload.php');

        // Define storage path
        $uploadDir = __DIR__ . '/data/profiles/';
        if (!is_dir($uploadDir)) {
            mkdir($uploadDir, 0755, true); // create directory if not exists
        }

        try {
            // Use the user ID as filename, no extension
            $file = file_upload('file', $uploadDir, $id);
        } catch (FileUploadException $e) {
            error_log($e);
            http_response_code(500);
            echo json_encode(['error' => $e]);
            exit;
        }

        // Resize image
        if (in_array(exif_imagetype($file), SUPPORTED_IMAGETYPE)) {
            $image = new SimpleImage();
            $image->load($file);
            $image->resizeToHeight(200);
            $image->save($file);
        }

        // OK
        http_response_code(200);
        echo json_encode(['success' => true, 'path' => 'profiles/' . $id]);
        exit;
    }

    http_response_code(400);
    echo json_encode(['error' => 'Bad request']);
    exit;
}

function post_attachment_upload(string $id)
{
    require_once('src/file_upload.php');

    // Define storage path
    $uploadDir = __DIR__ . '/data/attachments/';
    if (!is_dir($uploadDir)) {
        mkdir($uploadDir, 0755, true); // create directory if not exists
    }

    try {
        // Download file
        $file = file_upload('file', $uploadDir, $id);

        // Make a thumbnail for images
        if (in_array(exif_imagetype($file), SUPPORTED_IMAGETYPE)) {
            $image = new SimpleImage();
            $image->load($file);
            $image->resizeToHeight(250);
            $image->save($uploadDir . "thumbnail/" . $id);
        }

        // Tell core we received it
        attachment_update_status($id, "STORED");
    } catch (FileUploadException $e) {
        // Tell core something went wrong
        attachment_update_status($id, "CORRUPT");

        error_log($e);
        http_response_code(500);
        echo json_encode(['error' => $e]);
        exit;
    }
}
