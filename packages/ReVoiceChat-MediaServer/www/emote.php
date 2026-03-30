<?php
require_once 'src/files.php';
require_once 'src/tools.php';

const CONTENT_TYPE_APPLICATION_JSON = "Content-Type: application/json";
const SUPPORTED_IMAGETYPE = [IMAGETYPE_JPEG, IMAGETYPE_GIF, IMAGETYPE_PNG];

$body = json_decode(file_get_contents('php://input'), true, 512, JSON_OBJECT_AS_ARRAY);

switch ($_SERVER["REQUEST_METHOD"]) {
    case 'GET':
        // Emojis Any
        if (isset($_GET['any']) && isset($_GET['id']) && !empty($_GET['id'])) {
            get_emoji_any($_GET['id']);
            break;
        }

        // Emojis global only
        if (isset($_GET['global'])) {
            if (isset($_GET['all'])) {
                get_emojis_all('global');
                break;
            }

            if (isset($_GET['id']) && !empty($_GET['id'])) {
                rvc_read_file('emojis/global', $_GET['id']);
                break;
            }
        }

        error_log("Invalid endpoint " . $_SERVER['REQUEST_URI'] . " Method " . $_SERVER["REQUEST_METHOD"]);
        http_response_code(400);
        break;

    case 'POST':
        if (isset($_GET['any']) && isset($_GET['id'])) {
            post_emoji_upload($_GET['id']);
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

function get_emoji_any(string $name)
{
    $rootDir = __DIR__ . "/data/emojis";

    // Global
    if (is_file("$rootDir/global/$name")) {
        rvc_read_file("emojis/global", $name);
        exit;
    }

    // Any
    if (is_file("$rootDir/$name")) {
        rvc_read_file("emojis/", $name);
        exit;
    }

    http_response_code(404);
    exit;
}

function get_emojis_all(string $where)
{
    $workingDirectory = __DIR__ . "/data/emojis/$where/";
    if (!is_dir($workingDirectory)) {
        http_response_code(404);
        exit;
    }

    $list = scandir($workingDirectory);
    $list = array_values(array_diff($list, [".", "..", ".keep"]));

    header(CONTENT_TYPE_APPLICATION_JSON);
    echo json_encode($list);

    exit;
}

function post_emoji_upload(string $id)
{
    require_once('src/file_upload.php');

    // Define storage path
    $uploadDir = __DIR__ . "/data/emojis/";
    if (!is_dir($uploadDir)) {
        mkdir($uploadDir, 0755, true); // create directory if not exists
    }

    try {
        $file = file_upload('file', $uploadDir, $id);

        // Resize image
        if (in_array(exif_imagetype($file), SUPPORTED_IMAGETYPE)) {
            $image = new SimpleImage();
            $image->load($file);
            $image->resizeToHeight(64);
            $image->save($file);
        }

        // Tell core we received it
        attachment_update_status($id, "STORED");
    } catch (FileUploadException $e) {
        // Tell core something went wrong
        attachment_update_status($id, "CORRUPT");

        http_response_code(500);
        echo json_encode(['error' => $e]);
        exit;
    }

    http_response_code(200);
    echo json_encode(['success' => true, 'path' => 'emojis/' . $id]);
    exit;
}
