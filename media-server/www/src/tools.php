<?php

function url_with_id($type, &$matches)
{
    return preg_match('#^.*/' . $type . '/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$#', $_SERVER['REQUEST_URI'], $matches);
}


function authorization_header()
{
    if (isset($_SERVER['HTTP_AUTHORIZATION'])) {
        return $_SERVER['HTTP_AUTHORIZATION'];
    }
    if (isset($_SERVER['REDIRECT_HTTP_AUTHORIZATION'])) {
        return $_SERVER['REDIRECT_HTTP_AUTHORIZATION'];
    }
    if (function_exists('apache_request_headers')) {
        $headers = apache_request_headers();
        if (isset($headers['Authorization'])) {
            return $headers['Authorization'];
        }
    }
    error_log("Missing Authorization Header");
    http_response_code(401);
    echo json_encode(['error' => 'Missing Authorization header']);
    exit;
}

function curl_core_no_auth($url) {
    return curl_core($url, null, null, false);
}

function curl_core(string $url, $data = null, $method = null, $auth_needed = true)
{
    $ch = curl_init($url);

    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

    // Method is set
    if(!empty($method)){
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
    }

    // Data available ?
    if (!empty($data)) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        if ($auth_needed) {
            curl_setopt($ch, CURLOPT_HTTPHEADER, [
                "Authorization:" . authorization_header(),
                "Content-Type:application/json"
            ]);
        } else {
            curl_setopt($ch, CURLOPT_HTTPHEADER, ["Content-Type:application/json"]);
        }
    }
    elseif ($auth_needed) {
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            "Authorization:" . authorization_header()
        ]);
    }

    $response = curl_exec($ch);

    if ($response === false) {
        $error = curl_error($ch);
        $errno = curl_errno($ch);
        curl_close($ch);
        http_response_code(500);

        error_log("cURL request failed: $error, $errno");

        echo json_encode([
            'error' => 'cURL request failed',
            'curl_error' => $error,
            'curl_errno' => $errno
        ]);
        exit;
    }

    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    if ($httpCode == 200) {
        return json_decode($response, true);
    }

    error_log("cURL request not OK,\nResponse: $response,\nHTTP Code: $httpCode");
    error_log("cURL Info:" . print_r(curl_getinfo($ch), true));

    http_response_code($httpCode);
    echo json_encode(
        [
            'error' => $response,
            'code' => $httpCode
        ]
    );
    exit;
}

function get_current_user_from_auth()
{
    $settings = parse_ini_file(__DIR__ . '/../../settings.ini', true);
    $url = $settings['api']['user_me_url'];
    return curl_core($url);
}

function attachment_update_status(string $id, string $status)
{
    $settings = parse_ini_file(__DIR__ . '/../../settings.ini', true);
    $url = $settings['api']['media_url'] . "/$id";
    curl_core($url, $status, "PATCH");
}
