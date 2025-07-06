# ✅ How large video uploads typically work
## 🔥 1. Client-side handling
If you just use a basic HTML `<input type="file" />` and submit it, the entire file is sent in a single HTTP POST request.

For large files (like 1GB), it’s better to split (chunk) the file on the client side, upload each chunk separately, and then "assemble" it on the server side. This approach is called chunked upload or resumable upload.

### ⚡ Why chunk?
More reliable (can retry failed chunks).

Supports resume if upload is interrupted.

Avoids browser or server timeouts for very large requests.

## 🔥 2. Backend-side handling
The backend receives each chunk and stores it temporarily (e.g., on disk or a cloud bucket).

Once all chunks are received, it assembles them into the final file.

After that, it can store metadata (file path, size, duration, etc.) in your database — not the entire video file itself, usually.

The video file is typically stored in:

Cloud object storage (S3, GCP Storage, Azure Blob, etc.)

A local file system (if scale is small)

The DB only stores a reference to the file (URL, path, ID).

## 💡 Underlying protocol
HTTP/HTTPS POST or PUT is the common protocol.

When using chunked uploads, each chunk can be sent as a separate HTTP request.

Sometimes protocols like multipart upload (e.g., S3 multipart) or WebSockets (for very interactive cases) are used.

## 💥 Flow diagram
Here’s a diagram to illustrate this:

```plaintext
[Client Browser or App]
   |
   |-- (1) Select large video file (e.g., 1GB)
   |
   |-- (2) Split file into smaller chunks (e.g., 5MB each)
   |
   |-- (3) For each chunk:
   |       |
   |       |--> Send HTTP POST /upload-chunk
   |       |
   |       |<-- Receive success/ack for chunk
   |
   |-- (4) After all chunks uploaded:
   |       |
   |       |--> Send HTTP POST /finalize-upload
   |       |    (includes file metadata, chunk list)
   |
   |<-- (5) Server confirms and returns final file URL or ID
   |
[Server Backend]
   |
   |-- Receive each chunk, store temporarily (disk or cloud)
   |
   |-- After all chunks: assemble file
   |
   |-- Store final video file (object storage or file system)
   |
   |-- Store metadata in DB (reference only)
```

# ✅ Summary points
Don’t store actual video files in DB — store in storage, save references in DB.

Use chunked/resumable uploads for large files.

Use HTTP POST/PUT, possibly with multipart/form-data for small files or resumable chunk APIs for large ones.

Add support for retry, resume, and progress tracking on the client.

⭐ If you'd like, I can also provide:
✅ A sample React or plain JS front-end file upload logic using chunks.
✅ A Spring Boot or Node.js backend example for chunk assembly.
✅ A prettier diagram (SVG or image) if you'd like to embed it in docs.

Just ask! 🚀