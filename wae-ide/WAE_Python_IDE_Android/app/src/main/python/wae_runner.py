import contextlib
import io
import json
import traceback

def run_code(source: str) -> str:
    """Run user code in a persistent WAE namespace and return JSON output."""
    stdout = io.StringIO()
    stderr = io.StringIO()
    if not hasattr(run_code, "_namespace"):
        run_code._namespace = {"__name__": "__main__"}
    namespace = run_code._namespace

    try:
        compiled = compile(source, "<wae-editor>", "exec")
        with contextlib.redirect_stdout(stdout), contextlib.redirect_stderr(stderr):
            exec(compiled, namespace, namespace)
        return json.dumps({
            "stdout": stdout.getvalue(),
            "stderr": stderr.getvalue(),
            "line": 0,
            "ok": True
        })
    except BaseException as exc:
        trace = traceback.TracebackException.from_exception(exc)
        line = 1
        for frame in traceback.extract_tb(exc.__traceback__):
            if frame.filename == "<wae-editor>":
                line = frame.lineno
        return json.dumps({
            "stdout": stdout.getvalue(),
            "stderr": "".join(trace.format()),
            "line": line,
            "ok": False
        })
