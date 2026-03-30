<?php

const FORMAT_ALLOW_PREVIEW = [
    "image/jpeg",
    "image/bmp",
    "image/png",
    "application/pdf"
];

function rvc_read_file(string $where, string $name)
{
    $file = __DIR__ . "/../data/$where/$name";

    if (!file_exists($file)) {
        if ($where == "profiles" && $name != "default-avatar") {
            rvc_read_file("profiles", "default-avatar");
            exit;
        }
        http_response_code(404);
        exit;
    }

    $type = mime_content_type($file);

    header("Content-Disposition: inline");
    header("Content-Type: $type");
    readfile($file);

    exit;
}

function rvc_download_file(string $where, string $name)
{
    require_once "tools.php";

    $settings = parse_ini_file(__DIR__ . '/../../settings.ini', true);
    $file = __DIR__ . "/../data/$where/$name";

    if (!file_exists($file)) {
        http_response_code(404);
        exit;
    }

    $url = $settings['api']['media_url'] . "/$name";
    $core_info = curl_core_no_auth($url);
    $mime_type = mime_content_type($file);

    if(in_array($mime_type, FORMAT_ALLOW_PREVIEW)){
        header("Content-Disposition: inline");
        header("Content-Type: $type");
    }
    else{
        header('Content-Disposition: attachment; filename="' . $core_info['name'] . '"');
    }

    readfile($file);
    exit;
}

function rvc_file_exists(string $where, string $name)
{
    $file = __DIR__ . "/../data/$where/$name";

    if (file_exists($file)) {
        http_response_code(200);
        exit;
    } else {
        http_response_code(204);
        exit;
    }
}

function rvc_multiple_file_exists(string $where, iterable|object $names)
{
    $result = [];

    foreach ($names as $name) {
        $file = __DIR__ . "/../data/$where/$name";
        $result[$name] = file_exists($file);
    }

    http_response_code(200);
    header(CONTENT_TYPE_APPLICATION_JSON);
    echo json_encode($result);

    exit;
}