
const http = require("http");

const PORT = process.env.PORT || 3000;

const config = {
  geminiKey: process.env.GEMINI_API_KEY || "",
  groqKey: process.env.GROQ_API_KEY || "",
  geminiModel: process.env.GEMINI_MODEL || "",
  groqModel: process.env.GROQ_MODEL || "llama-3.3-70b-versatile"
};

const history = [];

const HTML = String.raw`<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="theme-color" content="#101827">
<title>الخبير الاقتصادي | ORANOS</title>
<style>
*{box-sizing:border-box}
body{margin:0;background:#0b1220;color:#edf2fa;font-family:Arial,sans-serif}
header{padding:23px 18px;background:linear-gradient(120deg,#17243b,#101827);border-bottom:1px solid #34415a}
h1{margin:0;color:#e5c477;font-size:25px}
.sub{color:#9baac0;font-size:12px;margin-top:8px}
main{max-width:900px;margin:auto;padding:17px}
.card{background:#141f31;border:1px solid #2b3a52;border-radius:15px;padding:16px;margin-bottom:15px}
h2{font-size:17px;color:#e5c477;margin-top:0}
input,textarea,select{width:100%;padding:13px;margin:7px 0 12px;background:#0b1423;color:white;border:1px solid #354761;border-radius:9px;font-size:14px}
textarea{min-height:110px;resize:vertical}
button{background:#bd984d;color:#111827;border:0;padding:13px 18px;border-radius:9px;font-weight:bold;cursor:pointer}
button:disabled{opacity:.5}
.secondary{background:#263650;color:white}
.status{color:#91cba8;font-size:12px}
#output{white-space:pre-wrap;line-height:1.9;overflow-wrap:anywhere}
.item{padding:10px;border-bottom:1px solid #29384c}
footer{text-align:center;color:#71829c;padding:20px;font-size:12px}
</style>
</head>
<body>
<header>
<h1>الخبير الاقتصادي</h1>
<div class="sub">THE ECONOMIC EXPERT · ORANOS INTELLIGENCE</div>
<div class="sub">العقل المركزي | فريق الخبراء | الاقتصاد العالمي</div>
</header>
<main>
<section class="card">
<h2>مركز القيادة</h2>
<div class="status" id="status">النظام جاهز لاستقبال المهام</div>
<p>أعطِ هدفك، والعقل المركزي يقسمه إلى مراحل ويستعين بفريق الخبراء.</p>
<textarea id="mission" placeholder="مثال: ابحث عن أفضل مجال رقمي يمكن أن يحقق دخلاً بالدولار، وقارن الفرص وضع خطة عملية..."></textarea>
<select id="engine">
<option value="auto">اختيار المحرك تلقائياً</option>
<option value="gemini">Gemini</option>
<option value="groq">Groq</option>
</select>
<button id="run" onclick="startMission()">ابدأ المهمة</button>
<button class="secondary" onclick="clearOutput()">مسح التقرير</button>
</section>

<section class="card">
<h2>إعدادات المحركات</h2>
<label>Gemini API Key</label>
<input id="gkey" type="password" placeholder="ضع مفتاح Gemini هنا">
<label>معرّف نموذج Gemini كما يظهر في AI Studio</label>
<input id="gmodel" placeholder="اكتب معرّف النموذج الصحيح">
<label>Groq API Key</label>
<input id="qkey" type="password" placeholder="ضع مفتاح Groq هنا">
<label>معرّف نموذج Groq</label>
<input id="qmodel" value="llama-3.3-70b-versatile">
<button onclick="saveSettings()">حفظ واختبار الإعدادات</button>
<div class="status" id="settingsStatus"></div>
</section>

<section class="card">
<h2>تقرير الفريق</h2>
<div id="output">في انتظار أول مهمة...</div>
</section>

<section class="card">
<h2>سجل المهام</h2>
<div id="history">لا توجد مهام بعد.</div>
</section>
</main>
<footer>ORANOS · THE ECONOMIC EXPERT · V1.0</footer>

<script>
async function request(url,data){
 const r=await fetch(url,{
  method:"POST",
  headers:{"Content-Type":"application/json"},
  body:JSON.stringify(data)
 });
 const j=await r.json();
 if(!r.ok)throw new Error(j.error||"حدث خطأ");
 return j;
}

async function saveSettings(){
 const s=document.getElementById("settingsStatus");
 s.textContent="جاري حفظ الإعدادات واختبار الاتصال...";
 try{
  const result=await request("/api/config",{
   geminiKey:document.getElementById("gkey").value,
   geminiModel:document.getElementById("gmodel").value,
   groqKey:document.getElementById("qkey").value,
   groqModel:document.getElementById("qmodel").value
  });
  s.textContent=result.message;
 }catch(e){s.textContent=e.message}
}

async function startMission(){
 const task=document.getElementById("mission").value.trim();
 if(!task){alert("اكتب المهمة أولاً");return}
 const btn=document.getElementById("run");
 const status=document.getElementById("status");
 const out=document.getElementById("output");
 btn.disabled=true;
 status.textContent="الفريق يدرس المهمة...";
 out.textContent="جاري التخطيط والتحليل ومراجعة النتائج...";
 try{
  const result=await request("/api/mission",{
   task:task,
   engine:document.getElementById("engine").value
  });
  out.textContent=result.report;
  status.textContent="اكتمل التحليل";
  loadHistory();
 }catch(e){
  out.textContent="تعذر إكمال المهمة: "+e.message;
  status.textContent="تحتاج المهمة إلى مراجعة";
 }finally{btn.disabled=false}
}

async function loadHistory(){
 try{
  const r=await fetch("/api/history");
  const j=await r.json();
  document.getElementById("history").innerHTML="";
  j.items.forEach(x=>{
   const d=document.createElement("div");
   d.className="item";
   d.textContent=x.date+" — "+x.task;
   document.getElementById("history").appendChild(d);
  });
 }catch(e){}
}
function clearOutput(){
 document.getElementById("output").textContent="في انتظار المهمة القادمة...";
}
loadHistory();
</script>
</body>
</html>`;

function send(res, code, data) {
  res.writeHead(code, {
    "Content-Type": "application/json; charset=utf-8",
    "Cache-Control": "no-store"
  });
  res.end(JSON.stringify(data));
}

async function readBody(req) {
  let raw = "";
  for await (const chunk of req) {
    raw += chunk;
    if (raw.length > 100000) throw new Error("الطلب كبير جداً");
  }
  return JSON.parse(raw || "{}");
}

async function callGemini(prompt) {
  if (!config.geminiKey || !config.geminiModel)
    throw new Error("أدخل مفتاح Gemini ومعرّف النموذج في الإعدادات");

  const url =
    "https://generativelanguage.googleapis.com/v1beta/models/" +
    encodeURIComponent(config.geminiModel) +
    ":generateContent?key=" + encodeURIComponent(config.geminiKey);

  const response = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      contents: [{ parts: [{ text: prompt }] }],
      generationConfig: { temperature: 0.4 }
    })
  });

  const data = await response.json();
  if (!response.ok)
    throw new Error(data.error?.message || "Gemini API error");

  return (data.candidates?.[0]?.content?.parts || [])
    .map(p => p.text || "").join("\n");
}

async function callGroq(prompt) {
  if (!config.groqKey)
    throw new Error("أدخل مفتاح Groq في الإعدادات");

  const response = await fetch(
    "https://api.groq.com/openai/v1/chat/completions",
    {
      method: "POST",
      headers: {
        "Authorization": "Bearer " + config.groqKey,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        model: config.groqModel,
        messages: [{ role: "user", content: prompt }],
        temperature: 0.4
      })
    }
  );

  const data = await response.json();
  if (!response.ok)
    throw new Error(data.error?.message || "Groq API error");

  return data.choices?.[0]?.message?.content || "";
}

async function ask(prompt, engine) {
  if (engine === "gemini") return callGemini(prompt);
  if (engine === "groq") return callGroq(prompt);

  if (config.geminiKey && config.geminiModel) {
    try { return await callGemini(prompt); }
    catch (e) {
      if (!config.groqKey) throw e;
    }
  }
  return callGroq(prompt);
}

async function runTeam(task, engine) {
  const plan = await ask(
    "أنت العقل المركزي لشركة الخبير الاقتصادي. حلل طلب المستخدم، " +
    "ضع خطة عمل واقعية من مراحل، حدد البيانات الناقصة والمخاطر. " +
    "لا تدعي أنك بحثت في الإنترنت. المهمة: " + task,
    engine
  );

  const research = await ask(
    "أنت باحث اقتصادي ومحلل أسواق. اعتماداً على المهمة والخطة التالية، " +
    "حلل الفرص والمنافسة والتكاليف والمخاطر. ميز بين الحقائق والفرضيات، " +
    "ولا تخترع إحصائيات أو مصادر. إذا كانت البيانات الحية ضرورية فاطلب التحقق منها.\n" +
    "المهمة: " + task + "\nالخطة: " + plan,
    engine
  );

  const audit = await ask(
    "أنت مهندس جودة ومراجع مستقل. راجع الخطة والتحليل التاليين. " +
    "اكشف التناقضات والمبالغات والمعلومات غير المؤكدة، ثم اقترح تحسينات عملية.\n" +
    "الخطة: " + plan + "\nالتحليل: " + research,
    engine
  );

  return await ask(
    "أنت المدير التنفيذي. اكتب تقريراً نهائياً واضحاً بالعربية يتضمن: " +
    "القرار المقترح، خطة التنفيذ، المهام التالية، المخاطر، ما يحتاج إلى تحقق خارجي، " +
    "وما تم إنجازه فعلاً. لا تدعِ تنفيذ أي إجراء خارجي أو تحقيق دخل.\n" +
    "المهمة: " + task +
    "\nالخطة: " + plan +
    "\nبحث الفريق: " + research +
    "\nمراجعة الجودة: " + audit,
    engine
  );
}

const server = http.createServer(async (req, res) => {
  try {
    const url = new URL(req.url, "http://localhost");

    if (req.method === "GET" && url.pathname === "/") {
      res.writeHead(200, { "Content-Type": "text/html; charset=utf-8" });
      return res.end(HTML);
    }

    if (req.method === "GET" && url.pathname === "/api/history") {
      return send(res, 200, { items: history.slice(-30).reverse() });
    }

    if (req.method === "POST" && url.pathname === "/api/config") {
      const body = await readBody(req);
      if (body.geminiKey) config.geminiKey = body.geminiKey.trim();
      if (body.groqKey) config.groqKey = body.groqKey.trim();
      if (body.geminiModel) config.geminiModel = body.geminiModel.trim();
      if (body.groqModel) config.groqModel = body.groqModel.trim();

      return send(res, 200, {
        message: "تم حفظ الإعدادات في ذاكرة الخادم. اختبر المحرك بإرسال مهمة."
      });
    }

    if (req.method === "POST" && url.pathname === "/api/mission") {
      const body = await readBody(req);
      const task = String(body.task || "").trim();
      const engine = ["auto", "gemini", "groq"].includes(body.engine)
        ? body.engine : "auto";

      if (!task) return send(res, 400, { error: "المهمة فارغة" });

      const report = await runTeam(task, engine);
      history.push({
        task: task.slice(0, 180),
        date: new Date().toLocaleString("ar-MA"),
        report
      });
      if (history.length > 50) history.shift();

      return send(res, 200, { report });
    }

    send(res, 404, { error: "المسار غير موجود" });
  } catch (error) {
    send(res, 500, { error: error.message || "خطأ داخلي" });
  }
});

server.listen(PORT, "0.0.0.0", () => {
  console.log("Economic Expert running on port " + PORT);
});
