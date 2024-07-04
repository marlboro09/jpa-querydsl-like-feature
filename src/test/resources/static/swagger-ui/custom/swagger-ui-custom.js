window.onload = function() {
    const ui = SwaggerUIBundle({
        url: "/v3/api-docs",
        dom_id: "#swagger-ui",
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        layout: "StandaloneLayout",
        requestInterceptor: function(request) {
            const accessToken = localStorage.getItem("accessToken");
            const refreshToken = localStorage.getItem("refreshToken");
            if (accessToken) {
                request.headers["Authorization"] = "Bearer " + accessToken;
            }
            if (refreshToken) {
                request.headers["RefreshToken"] = refreshToken;
            }
            return request;
        }
    });

    ui.initOAuth({
        clientId: "your-client-id",
        clientSecret: "your-client-secret",
        realm: "your-realms",
        appName: "your-app-name"
    });

    document.getElementById("swagger-ui").innerHTML += `
    <div id="custom-tokens" style="margin: 10px;">
      <h3>Custom Tokens</h3>
      <label for="custom-access-token">Access Token:</label>
      <input type="text" id="custom-access-token" style="width: 300px;" /><br/>
      <label for="custom-refresh-token">Refresh Token:</label>
      <input type="text" id="custom-refresh-token" style="width: 300px;" /><br/>
      <button onclick="saveTokens()">Save Tokens</button>
    </div>
  `;

    window.saveTokens = function() {
        const accessToken = document.getElementById("custom-access-token").value;
        const refreshToken = document.getElementById("custom-refresh-token").value;
        if (accessToken) {
            localStorage.setItem("accessToken", accessToken);
        }
        if (refreshToken) {
            localStorage.setItem("refreshToken", refreshToken);
        }
        alert("Tokens saved!");
    };
};
