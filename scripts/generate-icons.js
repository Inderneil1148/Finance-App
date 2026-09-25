import fs from 'fs';
import path from 'path';
import zlib from 'zlib';

function createPNG(width, height, isMaskable = false) {
  // CRC table
  const crcTable = [];
  for (let n = 0; n < 256; n++) {
    let c = n;
    for (let k = 0; k < 8; k++) {
      if (c & 1) c = 0xedb88320 ^ (c >>> 1);
      else c = c >>> 1;
    }
    crcTable[n] = c;
  }

  function crc32(buf) {
    let crc = 0xffffffff;
    for (let i = 0; i < buf.length; i++) {
      crc = crcTable[(crc ^ buf[i]) & 0xff] ^ (crc >>> 8);
    }
    return (crc ^ 0xffffffff) >>> 0;
  }

  function makeChunk(type, data) {
    const len = data.length;
    const buf = Buffer.alloc(12 + len);
    buf.writeUInt32BE(len, 0);
    buf.write(type, 4, 4, 'ascii');
    data.copy(buf, 8);
    const crcVal = crc32(buf.subarray(4, 8 + len));
    buf.writeUInt32BE(crcVal, 8 + len);
    return buf;
  }

  // RGBA buffer: row has 1 filter byte (0) + width * 4 bytes
  const rowStride = 1 + width * 4;
  const rawData = Buffer.alloc(height * rowStride);

  const cx = width / 2;
  const cy = height / 2;
  const outerRadius = width * 0.45;
  const safeRadius = isMaskable ? width * 0.35 : width * 0.42;

  // Material You primary blue & tones
  // #0B57D0 -> 11, 87, 208
  // #1A73E8 -> 26, 115, 232
  // #D3E3FD -> 211, 227, 253
  // White -> 255, 255, 255

  for (let y = 0; y < height; y++) {
    const rowOffset = y * rowStride;
    rawData[rowOffset] = 0; // Filter None

    for (let x = 0; x < width; x++) {
      const pxOffset = rowOffset + 1 + x * 4;
      const dx = x - cx;
      const dy = y - cy;
      const dist = Math.sqrt(dx * dx + dy * dy);

      if (isMaskable) {
        // Full bleed background for maskable
        let r = 11, g = 87, b = 208, a = 255;
        // Inner symbol: Aether wallet / diamond
        if (dist < safeRadius) {
          // Inner card shape or diamond shape
          const absDx = Math.abs(dx);
          const absDy = Math.abs(dy);
          if (absDx + absDy < safeRadius * 0.8) {
            r = 255; g = 255; b = 255; // White diamond/logo
          }
        }
        rawData[pxOffset] = r;
        rawData[pxOffset + 1] = g;
        rawData[pxOffset + 2] = b;
        rawData[pxOffset + 3] = a;
      } else {
        // Rounded squircle / circle icon
        if (dist <= outerRadius) {
          let r = 11, g = 87, b = 208, a = 255; // Google Blue #0B57D0
          // Diamond / wallet emblem
          const absDx = Math.abs(dx);
          const absDy = Math.abs(dy);
          if (absDx + absDy < outerRadius * 0.65) {
            // White emblem
            r = 255; g = 255; b = 255;
          }
          // Soft edge antialiasing
          if (dist > outerRadius - 1.5) {
            a = Math.floor(255 * (outerRadius - dist) / 1.5);
          }
          rawData[pxOffset] = r;
          rawData[pxOffset + 1] = g;
          rawData[pxOffset + 2] = b;
          rawData[pxOffset + 3] = a;
        } else {
          rawData[pxOffset] = 0;
          rawData[pxOffset + 1] = 0;
          rawData[pxOffset + 2] = 0;
          rawData[pxOffset + 3] = 0;
        }
      }
    }
  }

  const deflated = zlib.deflateSync(rawData);

  // PNG Header
  const signature = Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]);

  // IHDR
  const ihdrData = Buffer.alloc(13);
  ihdrData.writeUInt32BE(width, 0);
  ihdrData.writeUInt32BE(height, 4);
  ihdrData[8] = 8; // Bit depth: 8
  ihdrData[9] = 6; // Color type: 6 (RGBA)
  ihdrData[10] = 0; // Compression
  ihdrData[11] = 0; // Filter
  ihdrData[12] = 0; // Interlace
  const ihdrChunk = makeChunk('IHDR', ihdrData);

  // IDAT
  const idatChunk = makeChunk('IDAT', deflated);

  // IEND
  const iendChunk = makeChunk('IEND', Buffer.alloc(0));

  return Buffer.concat([signature, ihdrChunk, idatChunk, iendChunk]);
}

const outDir = path.resolve('public');
if (!fs.existsSync(outDir)) {
  fs.mkdirSync(outDir, { recursive: true });
}

fs.writeFileSync(path.join(outDir, 'pwa-192x192.png'), createPNG(192, 192, false));
fs.writeFileSync(path.join(outDir, 'pwa-512x512.png'), createPNG(512, 512, false));
fs.writeFileSync(path.join(outDir, 'pwa-maskable-512x512.png'), createPNG(512, 512, true));
fs.writeFileSync(path.join(outDir, 'apple-touch-icon.png'), createPNG(180, 180, false));

// Also generate public/icon.svg
const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" fill="none">
  <rect width="512" height="512" rx="128" fill="#0B57D0"/>
  <path d="M256 112L392 248L256 384L120 248L256 112Z" fill="#FFFFFF"/>
  <circle cx="256" cy="248" r="48" fill="#0B57D0"/>
  <path d="M224 350L256 382L288 350" stroke="#FFFFFF" stroke-width="20" stroke-linecap="round" stroke-linejoin="round"/>
</svg>`;
fs.writeFileSync(path.join(outDir, 'icon.svg'), svg);

console.log('PWA Android Icons generated successfully in /public!');
