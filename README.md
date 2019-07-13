<h1 id="build-frontend">Build frontend</h1>
<pre><code>$ git clone https://github.com/lonewolf2110/kma-schedule-generator.git
</code></pre>
<p>Build Bootstrap 4.3.1 from source</p>
<pre><code>$ cd frontend/assets/bootstrap-4.3.1/
$ npm install
$ bundle install
$ npm run dist
</code></pre>
<p>I’ve created a bash script to build SCSS and bundle Javascript files then copy them into webapp folder</p>
<pre><code>$ cd frontend
$ ./build.sh
</code></pre>
<h1 id="google-drive-api-setup">Google Drive API setup</h1>
<p>Download file <strong>credentials.json</strong> from google api console to <strong>src/main/resources/gdrive</strong></p>
<pre><code>$ cd src/main/resources
$ mkdir gdrive &amp;&amp; cd gdrive
$ cp ~/Downloads/credentials.json credentials.json
$ mkdir tokens
</code></pre>
<p>More details here <a href="https://developers.google.com/drive/api/v3/about-sdk">Google Drive REST API v3</a></p>
<h1 id="changelog">Changelog</h1>
<p>I’ve forgot to add my Google drive credentials and tokens to .gitignore so i deleted all my commits before <strong>v1.0.21</strong>.<br>
There are some major updates</p>
<ul>
<li><strong>v1.0.21</strong> to <strong>v1.1.21</strong>: load time has been significantly reduced by using JSoup instead of HtmlUnit to get schedule excel file from KMA server.</li>
<li><strong>v0.0.21b</strong> to <strong>v1.0.21</strong>: archived generated file on Google drive instead of localhost.</li>
<li><strong>v0.0.21a</strong> to <strong>v0.0.21b</strong>: resolved IDM download problem by archived generated file on localhost and sent the download link to client instead of sent blob data directly.</li>
</ul>
<h1 id="creator">Creator</h1>
<p><strong>Trần Công</strong></p>
<ul>
<li><a href="https://facebook.com/lonewolf.2110">Facebook</a></li>
<li><a href="https://twitter.com/lonewolft2110">Twitter</a></li>
<li><a href="https://github.com/lonewolf2110">Github</a></li>
</ul>
<h1 id="thanks">Thanks</h1>
<p><strong><a href="http://getbootstrap.com/">Bootstrap</a></strong><br>
<strong><a href="https://sweetalert2.github.io/">SweetAlert2</a></strong><br>
<strong><a href="https://daneden.github.io/animate.css/">Animate.css</a></strong><br>
<strong><a href="https://uicookies.com/">uiCookies</a></strong><br>
<strong><a href="https://github.com/axios/axios">Axios</a></strong><br>
<strong><a href="https://www.google.com/fonts/">Google Fonts</a></strong><br>
<strong><a href="https://fontawesome.com/">FontAwesome</a></strong><br>
<strong><a href="http://htmlunit.sourceforge.net/">HtmlUnit</a></strong><br>
<strong><a href="https://jsoup.org/">JSoup</a></strong><br>
<strong><a href="https://poi.apache.org/">Apache POI</a></strong><br>
<strong><a href="https://itextpdf.com/en">iText</a></strong><br>
<strong><a href="https://github.com/google/gson">gson</a></strong><br>
<strong><a href="https://developers.google.com/drive/">Google Drive API</a></strong></p>

